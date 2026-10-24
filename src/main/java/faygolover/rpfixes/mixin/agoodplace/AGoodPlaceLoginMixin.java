package faygolover.rpfixes.mixin.agoodplace;

import faygolover.rpfixes.fix.AGoodPlaceTags;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * A Good Place 1.2.8, клиент: на входе на сервер мод разбирает свои списки блоков с тегами, а теги сервер шлёт позже.
 * При первом входе после запуска игры тегов нет совсем → «Missing tag minecraft:flowers», и событие входа обрывается
 * на нём. Здесь разбор на входе пропускается — его делает {@link AGoodPlaceTags} по приходу тегов.
 */
@Pseudo
@Mixin(targets = "nl.enjarai.a_good_place.forge.AGoodPlaceImpl", remap = false)
public abstract class AGoodPlaceLoginMixin {

    @Inject(method = "onLevelLoad", at = @At("HEAD"), cancellable = true)
    private void rpfixes$waitForTags(ClientPlayerNetworkEvent.LoggingIn event, CallbackInfo ci) {
        if (AGoodPlaceTags.enabled()) ci.cancel();
    }
}
