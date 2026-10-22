package faygolover.rpfixes.mixin.tips;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Tips 12.1.9, клиент: свои цветные подсказки мод рисует поверх загрузки мира, подключения, отключения, сохранения,
 * паузы и экрана смерти. В меню сборки на экранах загрузки уже есть свои «ЗАМЕТКИ» терминала (FancyMenu), и подсказки
 * шли двумя слоями. Здесь мод рисует только на паузе (в углу) и на экране смерти; хук на сам рисунок, а не на список
 * экранов — у отдельной подсказки может быть свой список экранов.
 */
@Mixin(targets = "net.darkhax.tipsmod.impl.client.TipRenderHandler", remap = false)
public abstract class TipRenderHandlerMixin {

    @Inject(method = "drawTip", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$pauseOnly(GuiGraphics graphics, Screen screen, CallbackInfo ci) {
        if (enabled() && !(screen instanceof PauseScreen) && !(screen instanceof DeathScreen)) ci.cancel();
    }

    private static boolean enabled() {
        try {
            return FixesConfig.TIPS_PAUSE_ONLY.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }
}
