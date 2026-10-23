package faygolover.rpfixes.mixin.glowsticks;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Glow Sticks 7.5.1: невидимый блок света за летящей ХИС (и в воде) — 8 вместо 15. */
@Mixin(targets = "de.markusbordihn.glowsticks.block.GlowStickBlockProperties", remap = false)
public abstract class GlowStickTrailLightMixin {

    @Inject(method = "createLightBlockProperties", at = @At("RETURN"))
    private static void rpfixes$trail8(CallbackInfoReturnable<BlockBehaviour.Properties> cir) {
        cir.getReturnValue().lightLevel(state -> 8);
    }
}
