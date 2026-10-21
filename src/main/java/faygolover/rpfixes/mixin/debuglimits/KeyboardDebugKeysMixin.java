package faygolover.rpfixes.mixin.debuglimits;

import faygolover.rpfixes.fix.PlayerDebugLimits;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** F3+B, F3+G, F3+H у игроков без op 2 ничего не делают — см. {@link PlayerDebugLimits}. */
@Mixin(KeyboardHandler.class)
public abstract class KeyboardDebugKeysMixin {
    @Inject(method = "handleDebugKeys", at = @At("HEAD"), cancellable = true)
    private void rpfixes$noCheatKeys(int key, CallbackInfoReturnable<Boolean> cir) {
        if (PlayerDebugLimits.blocksKey(key)) cir.setReturnValue(true);  // «обработано»: F3 не переключится при отпускании
    }
}
