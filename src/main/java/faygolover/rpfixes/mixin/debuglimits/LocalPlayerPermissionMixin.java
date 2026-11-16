package faygolover.rpfixes.mixin.debuglimits;

import faygolover.rpfixes.s5.PlayerDebugLimits;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Сервер шлёт уровень прав при входе и при смене op — тогда один раз и выключаем — см. {@link PlayerDebugLimits}. */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerPermissionMixin {
    @Inject(method = "setPermissionLevel", at = @At("TAIL"))
    private void rpfixes$applyDebugLimits(int level, CallbackInfo ci) {
        PlayerDebugLimits.onPermissionLevel();
    }
}
