package faygolover.rpfixes.mixin.securitycraft;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * SecurityCraft, камера: вращающаяся камера на сервере каждый тик зовёт sendBlockUpdated — весь блок (владелец, опции,
 * модули) всем, кто видит чанк (780 пакетов/с, 341 КБ/с на живом 31.10). Клиент крутит камеру сам тем же tick; сверка с
 * сервером — раз в {@code securitycraft_camera_sync_seconds}. Изменение опций камеры мод шлёт отдельно, как и раньше.
 */
@Mixin(targets = "net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity", remap = false)
public abstract class CameraRotationSyncMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
    private void rpfixes$rare(Level level, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        int seconds = FixesConfig.SECURITYCRAFT_CAMERA_SYNC_SECONDS.get();
        if (seconds <= 0 || Math.floorMod(level.getGameTime() + pos.asLong(), seconds * 20L) == 0) level.sendBlockUpdated(pos, oldState, newState, flags);
    }
}
