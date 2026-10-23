package faygolover.rpfixes.mixin.glowsticks;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Glow Sticks 7.5.1: лежащая ХИС светит не ярче 8 (было 15), гаснет так же ровно — 15 ступеней мода сжаты в 8..1;
 * под управлением редстоуна сила сигнала тоже сжимается до 8, без сигнала — 0. Яркость состояния блока считается один
 * раз при запуске игры, поэтому значение постоянное, без настройки.
 */
@Mixin(targets = "de.markusbordihn.glowsticks.block.glowstick.BlockStateManager", remap = false)
public abstract class GlowStickLightMixin {

    @Inject(method = {"calculateLightLevel", "calculateCreativeLightLevel"}, at = @At("RETURN"), cancellable = true)
    private static void rpfixes$max8(CallbackInfoReturnable<Integer> cir) {
        int light = cir.getReturnValueI();
        cir.setReturnValue(light <= 0 ? 0 : Math.max(1, Math.round(light * 8 / 15f)));   // 15..1 → 8..1, 0 остаётся 0
    }
}
