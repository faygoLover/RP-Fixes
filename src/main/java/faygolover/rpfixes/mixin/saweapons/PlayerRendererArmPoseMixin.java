package faygolover.rpfixes.mixin.saweapons;

import faygolover.rpfixes.fix.SaWeaponPoses;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Поза руки игрока с оружием Survivor's Arsenal ({@link SaWeaponPoses}). Через выбор позы ванилы: двуручная поза
 * в основной руке сама освобождает вторую, левши и предмет во второй руке — как у ванильного арбалета.
 */
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererArmPoseMixin {
    @Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$saWeaponPose(AbstractClientPlayer player, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        HumanoidModel.ArmPose pose = SaWeaponPoses.pose(player, hand);
        if (pose != null) cir.setReturnValue(pose);
    }
}
