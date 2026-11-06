package faygolover.rpfixes.mixin.cufflocks;

import com.lazrproductions.cuffed.api.CuffedAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cuffed: взлом отмычкой (навесной замок, дверь камеры и сейф, наручники) — только с особенностью «Взлом». Сервер шлёт
 * клиенту «начать взлом» тремя методами — без «Взлома» пакет не уходит и мини-игра не открывается.
 */
@Mixin(value = CuffedAPI.Networking.class, remap = false)
public abstract class CuffedLockpickMixin {
    @Inject(method = "sendLockpickBeginPickingLockPacketToClient", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rpfixes$lock(ServerPlayer player, int lockId, int speed, int progress, CallbackInfo ci) {
        if (faygolover.rpfixes.fix.PerkLocks.denyLockpick(player)) ci.cancel();
    }

    @Inject(method = "sendLockpickBeginPickingRestraintPacketToClient", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rpfixes$restraint(ServerPlayer player, String restrained, int type, int speed, int progress, CallbackInfo ci) {
        if (faygolover.rpfixes.fix.PerkLocks.denyLockpick(player)) ci.cancel();
    }

    @Inject(method = "sendLockpickBeginPickingCellDoorPacketToClient", at = @At("HEAD"), cancellable = true, require = 0)
    private static void rpfixes$cellDoor(ServerPlayer player, BlockPos pos, int speed, int progress, CallbackInfo ci) {
        if (faygolover.rpfixes.fix.PerkLocks.denyLockpick(player)) ci.cancel();
    }
}
