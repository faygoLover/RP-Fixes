package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import faygolover.rpfixes.mixin.customnpcs.CnpcSchedulerAccess;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Custom NPCs держит свой пул задач ({@code CustomNPCsScheduler}, {@code Executors.newScheduledThreadPool(1)}): его
 * поток — не фоновый, и пул никто не закрывает. После остановки сервера Java ждёт этот поток, процесс висит
 * (AllTheLeaks: «Stuck Thread: pool-N-thread-1 … customnpcs»). Здесь потоки пула делаются фоновыми — ещё до первой
 * задачи CNPC (конструктор мода раньше его setup), — а на выделенном сервере пул после остановки ещё и закрывается.
 */
public final class CnpcScheduler {
    private CnpcScheduler() {}

    /** Из конструктора мода (настройки ещё не прочитаны — фоновые потоки безопасны всегда, без настройки). */
    public static void init() {
        if (!ModList.get().isLoaded("customnpcs")) return;
        try {
            if (CnpcSchedulerAccess.rpfixes$executor() instanceof ThreadPoolExecutor pool) {
                AtomicInteger n = new AtomicInteger();
                pool.setThreadFactory(r -> {
                    Thread t = new Thread(r, "Custom NPCs scheduler " + n.incrementAndGet());
                    t.setDaemon(true);
                    return t;
                });
            }
        } catch (Throwable t) {
            RpFixes.LOGGER.warn("RP Fixes: пул Custom NPCs не сделать фоновым: {}", t.toString());
        }
    }

    public static void onServerStopped(ServerStoppedEvent e) {
        if (FMLEnvironment.dist.isClient() || !ModList.get().isLoaded("customnpcs") || !FixesConfig.CNPC_SCHEDULER_SHUTDOWN.get()) return;
        try {
            ScheduledExecutorService pool = CnpcSchedulerAccess.rpfixes$executor();
            pool.shutdownNow();
            RpFixes.LOGGER.info("RP Fixes: пул задач Custom NPCs закрыт после остановки сервера");
        } catch (Throwable t) {
            RpFixes.LOGGER.warn("RP Fixes: пул Custom NPCs не закрыть: {}", t.toString());
        }
    }
}
