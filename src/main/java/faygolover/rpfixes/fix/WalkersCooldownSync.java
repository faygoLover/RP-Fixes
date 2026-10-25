package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.Map;
import java.util.WeakHashMap;

/** Walkers: откат способности уходит игроку, только когда изменился (грузится только при установленном Walkers). */
public final class WalkersCooldownSync {
    private WalkersCooldownSync() {}

    private static final Map<ServerPlayer, Integer> LAST = new WeakHashMap<>();

    /** Откат изменился с последней отправки этому игроку (или её ещё не было). */
    public static boolean changed(ServerPlayer player) {
        if (!FixesConfig.WALKERS_COOLDOWN_ON_CHANGE.get()) return true;
        int cd = ((tocraft.walkers.impl.PlayerDataProvider) player).walkers$getAbilityCooldown();
        synchronized (LAST) {
            Integer last = LAST.put(player, cd);
            return last == null || last != cd;
        }
    }

    /** Новая сущность игрока у клиента (смена мира) — откат отправить заново. */
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) {
        if (e.getEntity() instanceof ServerPlayer p) synchronized (LAST) { LAST.remove(p); }
    }
}
