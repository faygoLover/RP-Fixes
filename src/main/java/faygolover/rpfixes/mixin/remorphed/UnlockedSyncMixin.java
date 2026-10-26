package faygolover.rpfixes.mixin.remorphed;

import faygolover.rpfixes.fix.CraftedCoreSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * RemorphED 4.x: список открытых морфов (remorphed:unlocked_sync) уходит игроку каждый тик (PlayerEntityMixin) —
 * 20 пакетов в секунду на игрока. Теперь — только если список изменился (см. CraftedCoreSync.unlocked).
 */
@Mixin(targets = "tocraft.remorphed.Remorphed", remap = false)
public abstract class UnlockedSyncMixin {
    @Redirect(method = "sync(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE",
            target = "Ltocraft/craftedcore/network/ModernNetworking;sendToPlayer(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;)V"))
    private static void rpfixes$onlyChanged(ServerPlayer target, ResourceLocation id, CompoundTag tag) {
        CraftedCoreSync.unlocked(target, id, tag);
    }
}
