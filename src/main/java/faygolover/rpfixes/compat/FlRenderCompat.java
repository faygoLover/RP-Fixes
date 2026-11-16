package faygolover.rpfixes.compat;

import net.minecraftforge.fml.ModList;

/**
 * FL Render (необязательный): светит ли сейчас настоящий свет фонаря. Классы FL Render трогаются только во
 * вложенном {@link Impl}, который грузится, лишь когда мод стоит. Нет FL Render или он старый (без этого вопроса)
 * — ответ «нет», и круг фонаря остаётся как у исходного мода.
 */
public final class FlRenderCompat {
    private FlRenderCompat() {}

    private static boolean usable = ModList.get().isLoaded("flrender");

    public static boolean flashlightLight() {
        if (!usable) return false;
        try {
            return Impl.flashlightLight();
        } catch (LinkageError oldFlRender) {
            usable = false;
            return false;
        }
    }

    private static final class Impl {
        static boolean flashlightLight() {
            return faygolover.flrender.api.FlRender.flashlightLight();
        }
    }
}
