package faygolover.rpfixes.mixin.debuglimits;

import faygolover.rpfixes.s5.PlayerDebugLimits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Субтитры у игроков без op 2 не включаются (проверка только в момент переключения) — см. {@link PlayerDebugLimits}. */
@Mixin(OptionInstance.class)
public abstract class SubtitlesOptionMixin {
    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    private void rpfixes$noSubtitles(Object value, CallbackInfo ci) {
        if (!Boolean.TRUE.equals(value)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.options == null || (Object) this != mc.options.showSubtitles()) return;
        if (PlayerDebugLimits.restricted()) ci.cancel();
    }
}
