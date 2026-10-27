package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.WeakHashMap;

/** ZeroContact: последнее отправленное игроку значение выносливости и её «вкл/выкл». */
public final class ZeroContactSync {
    private ZeroContactSync() {}

    private record Sent(float stamina, boolean enabled) {}
    private static final Map<ServerPlayer, Sent> LAST = new WeakHashMap<>();

    public static boolean changed(ServerPlayer p, float stamina, boolean enabled) {
        if (!FixesConfig.ZEROCONTACT_STAMINA_ON_CHANGE.get()) return true;
        Sent now = new Sent(stamina, enabled);
        synchronized (LAST) {
            if (now.equals(LAST.get(p))) return false;
            LAST.put(p, now);
            return true;
        }
    }
}
