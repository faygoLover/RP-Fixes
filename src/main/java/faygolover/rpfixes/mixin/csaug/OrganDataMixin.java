package faygolover.rpfixes.mixin.csaug;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.CsAugSync;
import net.corespring.csaugmentations.Augmentations.Base.Organs.SimpleHeart;
import net.corespring.csaugmentations.Utility.CSOrganTiers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * [CS] Augmentations 1.0.6.x: OrganData.updateOrganData() и organTick() идут каждый тик каждого игрока (ForgeEvents
 * playerInventory): пакет органов себе (S2CSyncDataPacket) и «снять и снова добавить» модификаторы скорости, урона,
 * скорости атаки, брони и здоровья (CyberSpeed / CyberAttackDamage / CyberAttackSpeed / CyberArmor / CyberHealth) —
 * атрибут каждый тик «изменён», UpdateAttributes игроку и всем, кто его видит. Здесь пакет — только при изменении
 * органов, модификатор меняется, только если его величина другая. Итоговые значения те же.
 */
@Mixin(targets = "net.corespring.csaugmentations.Capability.OrganCap$OrganData", remap = false)
public abstract class OrganDataMixin {
    @Shadow private Player player;
    @Shadow @Final private static UUID CyberHealth;
    @Shadow @Final private static UUID CyberAttackDamage;
    @Shadow @Final private static UUID CyberAttackSpeed;
    @Shadow @Final private static UUID CyberSpeed;
    @Shadow @Final private static UUID CyberArmor;

    @Shadow private double calculateLegBuffs(boolean legsEnabled) { return 0; }
    @Shadow private double[] calculateArmBuffs(boolean armsEnabled) { return null; }
    @Shadow private double calculateArmorBuffs() { return 0; }

    @Redirect(method = "updateOrganData", at = @At(value = "INVOKE",
            target = "Lnet/minecraftforge/network/simple/SimpleChannel;send(Lnet/minecraftforge/network/PacketDistributor$PacketTarget;Ljava/lang/Object;)V"))
    private void rpfixes$sendIfChanged(SimpleChannel channel, PacketDistributor.PacketTarget target, Object msg) {
        if (player instanceof ServerPlayer sp && !CsAugSync.organsChanged(sp, (net.minecraftforge.items.ItemStackHandler) (Object) this)) return;
        channel.send(target, msg);
    }

    @Inject(method = "applyEffects", at = @At("HEAD"), cancellable = true)
    private void rpfixes$applyEffects(boolean armsEnabled, boolean legsEnabled, CallbackInfo ci) {
        if (!FixesConfig.CSAUG_ON_CHANGE.get()) return;
        ci.cancel();
        if (player == null) return;
        double leg = calculateLegBuffs(legsEnabled);
        double[] arm = calculateArmBuffs(armsEnabled);
        double armor = calculateArmorBuffs();
        // как у мода (UUID урона и скорости атаки у него перепутаны — оставлено, иначе у игроков останутся старые модификаторы)
        CsAugSync.set(player.getAttribute(Attributes.MOVEMENT_SPEED), CyberSpeed, "CyberSpeed", leg, false);
        CsAugSync.remove(player.getAttribute(Attributes.ATTACK_DAMAGE), CyberAttackDamage);
        CsAugSync.set(player.getAttribute(Attributes.ATTACK_DAMAGE), CyberAttackSpeed, "CyberAttackDamage", arm[0], false);
        CsAugSync.remove(player.getAttribute(Attributes.ATTACK_SPEED), CyberAttackSpeed);
        CsAugSync.set(player.getAttribute(Attributes.ATTACK_SPEED), CyberAttackDamage, "CyberAttackSpeed", arm[1], false);
        if (armor > 0.0) CsAugSync.set(player.getAttribute(Attributes.ARMOR), CyberArmor, "CyberArmor", armor, true);
        else CsAugSync.remove(player.getAttribute(Attributes.ARMOR), CyberArmor);
    }

    @Inject(method = "handleHeart", at = @At("HEAD"), cancellable = true)
    private void rpfixes$handleHeart(CallbackInfo ci) {
        if (!FixesConfig.CSAUG_ON_CHANGE.get()) return;
        ci.cancel();
        if (player == null) return;
        ItemStack heart = ((net.minecraftforge.items.ItemStackHandler) (Object) this).getStackInSlot(12);
        Double value = null;
        if (heart.isEmpty()) value = CSOrganTiers.REMOVED.getDoubleAttribute(CSOrganTiers.Attribute.HEALTH);
        else if (heart.getItem() instanceof SimpleHeart h) value = h.getDoubleAttribute(CSOrganTiers.Attribute.HEALTH);
        if (value != null) CsAugSync.set(player.getAttribute(Attributes.MAX_HEALTH), CyberHealth, "CyberHealth", value, false);
    }
}
