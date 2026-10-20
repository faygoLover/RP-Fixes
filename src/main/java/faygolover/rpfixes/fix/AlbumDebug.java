package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Field;

/**
 * Диагностика альбома Exposure (фото не кладутся в альбом): пишет в лог путь «кнопка +» → клик по фото на клиенте и
 * на сервере — номер окна, класс меню, сторону, на которую кладём ({@code sideBeingAddedTo}), что в слоте и в курсоре.
 * Включается {@code fixes.debug_album}; пишет только пока открыто меню Exposure.
 */
public final class AlbumDebug {
    private AlbumDebug() {}

    private static boolean on() {
        try {
            return FixesConfig.DEBUG_ALBUM.get();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private static boolean exposure(AbstractContainerMenu menu) {
        return menu != null && menu.getClass().getName().startsWith("io.github.mortuusars.exposure");
    }

    private static String side(AbstractContainerMenu menu) {
        try {
            Field f = menu.getClass().getDeclaredField("sideBeingAddedTo");
            f.setAccessible(true);
            return String.valueOf(f.get(menu));
        } catch (Throwable t) {
            return "?";
        }
    }

    private static String stack(ItemStack s) {
        return s == null || s.isEmpty() ? "пусто" : s.getCount() + "x " + ForgeRegistries.ITEMS.getKey(s.getItem());
    }

    private static String slot(AbstractContainerMenu menu, int id) {
        if (id < 0 || id >= menu.slots.size()) return "слот " + id + " (вне меню)";
        Slot s = menu.slots.get(id);
        return "слот " + id + " " + s.getClass().getSimpleName() + " [" + stack(s.getItem()) + "] активен=" + s.isActive();
    }

    public static void clientButton(Player p, int containerId, int buttonId) {
        if (!on() || p == null || !exposure(p.containerMenu)) return;
        var screen = net.minecraft.client.Minecraft.getInstance().screen;
        RpFixes.LOGGER.info("[album] клиент: кнопка {} окна {} (открыто окно {} {}), экран {}", buttonId, containerId,
                p.containerMenu.containerId, p.containerMenu.getClass().getSimpleName(), screen == null ? "нет" : screen.getClass().getName());
    }

    public static void clientClick(Player p, int containerId, int slotId, int button, ClickType type) {
        if (!on() || p == null || !exposure(p.containerMenu)) return;
        RpFixes.LOGGER.info("[album] клиент: клик {} кн.{} {} окна {} | сторона {} | курсор [{}]", slot(p.containerMenu, slotId),
                button, type, containerId, side(p.containerMenu), stack(p.containerMenu.getCarried()));
    }

    public static void serverButton(ServerPlayer p, int containerId, int buttonId) {
        if (!on() || p == null || !exposure(p.containerMenu)) return;
        RpFixes.LOGGER.info("[album] сервер после: кнопка {} окна {} (открыто окно {} {}, stillValid={}) | сторона {}", buttonId,
                containerId, p.containerMenu.containerId, p.containerMenu.getClass().getSimpleName(), p.containerMenu.stillValid(p),
                side(p.containerMenu));
    }

    public static void serverClick(ServerPlayer p, int containerId, int slotId, int button, ClickType type) {
        if (!on() || p == null || !exposure(p.containerMenu)) return;
        RpFixes.LOGGER.info("[album] сервер после: клик {} кн.{} {} окна {} (открыто {}) | сторона {} | курсор [{}]",
                slot(p.containerMenu, slotId), button, type, containerId, p.containerMenu.containerId, side(p.containerMenu),
                stack(p.containerMenu.getCarried()));
    }
}
