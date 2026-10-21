package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.mixin.debuglimits.DebugRendererAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.lwjgl.glfw.GLFW;

/**
 * Игрокам без op 2 на сервере — без отладочных подсказок, которые дают лишнее знание: хитбоксы (F3+B, видны и невидимые
 * сущности), расширенные подсказки предметов (F3+H), границы чанков (F3+G), субтитры (сторона источника звука за
 * стеной), правая колонка F3 (настоящий id блока под прицелом — мины, ловушки, скрытые двери). Обычный F3 остаётся.
 * Без проверок каждый тик: выключается один раз, когда сервер сообщает уровень прав (вход, снятие op), а включить
 * обратно не дают сами переключатели. В одиночной игре и в главном меню ничего не ограничено.
 */
public final class PlayerDebugLimits {
    private PlayerDebugLimits() {}

    /** игрок на чужом сервере без op 2 */
    public static boolean restricted() {
        if (!FixesConfig.PLAYER_DEBUG_LIMITS.get()) return false;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer p = mc.player;
        return p != null && !mc.hasSingleplayerServer() && !p.hasPermissions(2);
    }

    /** F3+B, F3+G, F3+H */
    public static boolean blocksKey(int key) {
        return (key == GLFW.GLFW_KEY_B || key == GLFW.GLFW_KEY_G || key == GLFW.GLFW_KEY_H) && restricted();
    }

    /** сервер прислал уровень прав: без op 2 всё выключаем (настройки сохраняем, если что-то поменялось) */
    public static void onPermissionLevel() {
        if (!restricted()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getEntityRenderDispatcher().shouldRenderHitBoxes()) mc.getEntityRenderDispatcher().setRenderHitBoxes(false);
        if (((DebugRendererAccess) mc.debugRenderer).rpfixes$chunkBorder()) mc.debugRenderer.switchRenderChunkborder();
        boolean save = false;
        if (mc.options.advancedItemTooltips) {
            mc.options.advancedItemTooltips = false;
            save = true;
        }
        if (mc.options.showSubtitles().get()) {
            mc.options.showSubtitles().set(false);
            save = true;
        }
        if (save) mc.options.save();
    }
}
