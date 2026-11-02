package faygolover.rpfixes.mixin.butchery;

import faygolover.rpfixes.fix.BlockSyncOnChange;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Butchery, мясорубка (MCreator): процедура каждого тика после каждой записи в блок зовёт sendBlockUpdated — пустая
 * мясорубка дважды за тик шлёт весь блок всем, кто видит чанк. Обновление уходит, только если блок с прошлого раза изменился.
 */
@Mixin(targets = "net.mcreator.butchery.procedures.MeatgrinderrecipeprocedureProcedure", remap = false)
public abstract class MeatGrinderSyncMixin {
    @Redirect(method = "execute", at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/world/level/Level;sendBlockUpdated(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;I)V"))
    private static void rpfixes$onChange(Level level, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        if (BlockSyncOnChange.changed(level, pos)) level.sendBlockUpdated(pos, oldState, newState, flags);
    }
}
