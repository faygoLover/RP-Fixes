package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.core.RegistryAccess;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Method;

/**
 * A Good Place: списки блоков анимаций разбираются, когда клиент получил теги от сервера (на входе их ещё нет,
 * см. {@code AGoodPlaceLoginMixin}). Мод вызывается через отражение — сборка RP Fixes от него не зависит.
 */
public final class AGoodPlaceTags {
    private static Method populate;
    private static boolean failed;

    private AGoodPlaceTags() {}

    public static boolean enabled() {
        try {
            return FixesConfig.AGP_TAGS_AFTER_JOIN.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }

    public static void onTags(TagsUpdatedEvent e) {
        if (e.getUpdateCause() != TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED || failed || !enabled()) return;
        if (!ModList.get().isLoaded("a_good_place")) return;
        try {
            if (populate == null) {
                populate = Class.forName("nl.enjarai.a_good_place.pack.AnimationsManager").getMethod("populateTags", RegistryAccess.class);
            }
        } catch (Throwable t) {
            failed = true;
            RpFixes.LOGGER.warn("RP Fixes: A Good Place — не найден AnimationsManager.populateTags", t);
            return;
        }
        try {
            populate.invoke(null, e.getRegistryAccess());
        } catch (Throwable t) {
            RpFixes.LOGGER.warn("RP Fixes: A Good Place — списки блоков по тегам не разобраны", t.getCause() != null ? t.getCause() : t);
        }
    }
}
