package faygolover.rpfixes.mixin.worldedit;

import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.extension.platform.PlatformManager;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.WorldEditLazyStates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code Capability.WORLD_EDITING.ready()} (анонимный класс {@code Capability$4}) при старте сервера строит и регистрирует
 * все состояния всех блоков — в сборке их ~769 тыс., это ~3 ГБ кучи. Вместо этого запоминаем реестр блоков и регистрируем
 * состояния по типам, когда WorldEdit сам их построит ({@link WeBlockStateMixin}).
 */
@Mixin(targets = "com.sk89q.worldedit.extension.platform.Capability$4", remap = false)
public abstract class WorldEditingCapabilityMixin {
    @Inject(method = "ready", at = @At("HEAD"), cancellable = true)
    private void rpfixes$lazyReady(PlatformManager platformManager, Platform platform, CallbackInfo ci) {
        if (!FixesConfig.WORLDEDIT_LAZY_STATES.get()) return;
        WorldEditLazyStates.ready(platform);
        ci.cancel();
    }
}
