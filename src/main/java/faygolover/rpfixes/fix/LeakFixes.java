package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.ModList;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Заплатки от утечек памяти чужих модов (найдены по дампу кучи сервера «Департамент S5» 07.10.2026):
 * <ul>
 *   <li>Tacz Attribute Add 1.4.0 — {@code ShooterContext.shooterContext} (Map&lt;Thread, LivingEntity&gt;) держит
 *       последнего стрелка каждого потока, в том числе вышедшего игрока или убранного моба;</li>
 *   <li>Sanity: Descent Into Madness 1.1.0 — {@code InnerEntitySpawner.PLAYER_TO_SPAWN_TIMEOUT}
 *       (Map&lt;ServerPlayer, Integer&gt;) никогда не чистится: каждый вход оставляет игрока в памяти.</li>
 * </ul>
 * Чистим при выходе игрока и раз в минуту — убранные сущности. Поля ищем по имени; не нашли (другая версия
 * мода) — заплатка тихо выключается.
 */
public final class LeakFixes {
    private LeakFixes() {}

    private static final String TAA_CLASS = "com.xlxyvergil.taa.context.ShooterContext";
    private static final String TAA_FIELD = "shooterContext";
    private static final String SANITY_CLASS = "croissantnova.sanitydim.entity.InnerEntitySpawner";
    private static final String SANITY_FIELD = "PLAYER_TO_SPAWN_TIMEOUT";

    private static Map<?, ?> taa;
    private static Map<?, ?> sanity;
    private static boolean looked;

    private static void lookUp() {
        if (looked) return;
        looked = true;
        if (ModList.get().isLoaded("taa")) taa = staticMap(TAA_CLASS, TAA_FIELD);
        if (ModList.get().isLoaded("sanitydim")) sanity = staticMap(SANITY_CLASS, SANITY_FIELD);
    }

    private static Map<?, ?> staticMap(String cls, String field) {
        try {
            Field f = Class.forName(cls).getDeclaredField(field);
            f.setAccessible(true);
            Object v = f.get(null);
            if (v instanceof Map<?, ?> m) {
                RpFixes.LOGGER.info("RP Fixes: заплатка от утечки памяти включена для {}.{}", cls, field);
                return m;
            }
        } catch (Throwable t) {
            RpFixes.LOGGER.warn("RP Fixes: заплатка {}.{} не подошла к этой версии мода: {}", cls, field, t.toString());
        }
        return null;
    }

    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        lookUp();
        Entity p = e.getEntity();
        clean(p);
    }

    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || e.getServer().getTickCount() % 1200 != 0) return;
        lookUp();
        clean(null);
    }

    /** Убрать игрока {@code gone} (или только убранные сущности, если null). */
    private static void clean(Entity gone) {
        if (taa != null && FixesConfig.FIX_TAA_LEAK.get()) {
            synchronized (taa) {
                taa.values().removeIf(v -> v == gone || v instanceof Entity en && en.isRemoved());
            }
        }
        if (sanity != null && FixesConfig.FIX_SANITY_LEAK.get()) {
            synchronized (sanity) {
                sanity.keySet().removeIf(k -> k == gone || k instanceof Entity en && en.isRemoved());
            }
        }
    }
}
