package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import tocraft.craftedcore.data.PlayerDataSynchronizer;
import tocraft.craftedcore.network.ModernNetworking;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CraftedCore / Walkers (морфы): данные игрока уходят только при изменении, а не каждый тик.
 * <ul>
 * <li>себе — если пакет отличается от последнего отправленного этому игроку;</li>
 * <li>всем в мире — если отличается от последнего разосланного; тот, кто начал видеть игрока (вход, подошёл, сменил
 * мир), получает последние данные сразу (у клиента данные живут на сущности игрока — она создаётся заново);</li>
 * <li>после входа, возрождения и смены мира — полная отправка, как раньше.</li>
 * </ul>
 * Грузится только при установленном CraftedCore (миксин mixin.craftedcore). Откат Walkers — WalkersCooldownSync.
 */
public final class CraftedCoreSync {
    private CraftedCoreSync() {}

    private static final Map<UUID, CompoundTag> SELF = new ConcurrentHashMap<>(), ALL = new ConcurrentHashMap<>();

    private static boolean on() {
        return FixesConfig.CRAFTEDCORE_SYNC_ON_CHANGE.get();
    }

    public static void self(ServerPlayer player, ResourceLocation id, CompoundTag tag) {
        if (on()) {
            CompoundTag last = SELF.get(player.getUUID());
            if (tag.equals(last)) return;
            SELF.put(player.getUUID(), tag.copy());
        }
        ModernNetworking.sendToPlayer(player, id, tag);
    }

    public static void all(Iterable<ServerPlayer> players, ResourceLocation id, CompoundTag tag) {
        if (on() && tag.hasUUID("uuid")) {
            UUID who = tag.getUUID("uuid");
            if (tag.equals(ALL.get(who))) return;
            ALL.put(who, tag.copy());
        }
        ModernNetworking.sendToPlayers(players, id, tag);
    }

    private static void forget(PlayerEvent e) {
        if (!(e.getEntity() instanceof ServerPlayer p)) return;
        SELF.remove(p.getUUID());
        ALL.remove(p.getUUID());
    }

    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) { forget(e); }
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) { forget(e); }
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent e) { forget(e); }
    public static void onDimension(PlayerEvent.PlayerChangedDimensionEvent e) { forget(e); }

    /** Кто-то начал видеть игрока: его последние данные — этому зрителю (раньше они приходили со следующим тиком). */
    public static void onStartTracking(PlayerEvent.StartTracking e) {
        if (!on() || !(e.getTarget() instanceof ServerPlayer target) || !(e.getEntity() instanceof ServerPlayer viewer)) return;
        CompoundTag last = ALL.get(target.getUUID());
        if (last != null) ModernNetworking.sendToPlayer(viewer, PlayerDataSynchronizer.PLAYER_DATA_SYNC_ID, last.copy());
    }
}
