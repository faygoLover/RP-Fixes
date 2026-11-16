package faygolover.rpfixes.mixin.salocks;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Survivor's Arsenal: взлом замка отмычкой (ящик с лутом, дверь, навесной замок) — только с особенностью «Взлом».
 * Все три пути сходятся в {@code startSession}; без «Взлома» мини-игра не открывается. Тип цели — закрытый enum мода.
 */
@Pseudo
@Mixin(targets = "com.ogaba.sa_decor.common.loot.DecorLockpickManager", remap = false)
public abstract class SaLockpickMixin {
    @Inject(method = "startSession", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void rpfixes$hackerOnly(ServerPlayer player, Level level, BlockPos pos, InteractionHand hand, @Coerce Object type,
                                           CallbackInfoReturnable<InteractionResult> cir) {
        if (faygolover.rpfixes.s5.PerkLocks.denyLockpick(player)) cir.setReturnValue(InteractionResult.FAIL);
    }
}
