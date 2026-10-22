package faygolover.rpfixes.mixin.fancymenu;

import de.keksuccino.fancymenu.customization.ScreenCustomization;
import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayer;
import de.keksuccino.fancymenu.customization.layer.ScreenCustomizationLayerHandler;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * FancyMenu 3.9.6, клиент: задержка появления «только в первый раз» ({@code appearance_delay = first_time}) считалась
 * отыгранной, лишь когда элемент успевал появиться. Ушёл с главного меню раньше конца заставки (кнопка «1» сразу после
 * запуска) — при выходе из мира недоигранные строки (приветствие, карточка пользователя, панель «СИСТЕМА», клавиши)
 * снова выезжают с задержками, и заставка играет второй раз. Здесь при уходе с экрана такие элементы отмечаются
 * отыгранными: второй и следующие визиты за запуск игры — сразу без анимации.
 */
@Mixin(value = ScreenCustomization.class, remap = false)
public abstract class ScreenCustomizationFirstTimeMixin {

    @Inject(method = "onSwitchingToNewScreenType", at = @At("HEAD"))
    private static void rpfixes$markFirstTimeShown(Screen newScreen, Screen lastScreen, CallbackInfo ci) {
        if (lastScreen == null || !enabled()) return;
        ScreenCustomizationLayer layer = ScreenCustomizationLayerHandler.getLayerOfScreen(lastScreen);
        if (layer == null) return;
        for (AbstractElement e : layer.allElements) {
            if (e.appearanceDelay == AbstractElement.AppearanceDelay.FIRST_TIME) {
                e.getMemory().putProperty("appearance_delay_applied", true);
            }
        }
    }

    private static boolean enabled() {
        try {
            return FixesConfig.FANCYMENU_FIRST_TIME_ON_LEAVE.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }
}
