package faygolover.rpfixes.fix;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Pair;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import faygolover.rpfixes.mixin.emotecraft.EmoteServerNetworkAccess;
import io.github.kosmx.emotes.api.events.server.ServerEmoteAPI;
import io.github.kosmx.emotes.common.network.EmotePacket;
import io.github.kosmx.emotes.common.network.objects.NetData;
import io.github.kosmx.emotes.server.network.AbstractServerEmotePlay;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Emotecraft, сервер: зацикленная эмоция (лечь, сесть) считается играющей, пока от игрока не придёт «стоп». Если он
 * потерялся (у самого игрока эмоция уже кончилась), сервер шлёт её с текущего кадра каждому, кто начинает видеть
 * игрока, — у других он навсегда застывает лёжа, хотя ходит (жалоба 27.10). Здесь: игрок отошёл от места, где начал
 * эмоцию, дальше {@code emote_stop_distance} — сервер сам заканчивает её и рассылает «стоп» всем (и ему).
 * Принудительные эмоции (от сервера или команды) не трогаем.
 * <p>Выравнивание (1.5.4): клиент чужого игрока снимает эмоцию только по «стопу» с её UUID, а «стоп» мог потеряться
 * или прийти раньше самой эмоции — лёг, а сам уже ходит. Сервер помнит последнюю эмоцию каждого игрока; раз в
 * {@code emote_resync_interval} тиков, если сейчас эмоции нет, а последняя кончилась не дольше {@code emote_resync_window}
 * секунд назад, всем, кто его видит, снова уходит «стоп» этой эмоции (несколько байт). Играющим сейчас ничего не шлём:
 * пакет самой эмоции большой.
 */
public final class EmoteStale {
    private EmoteStale() {}

    private record Mark(UUID emote, int tick, Vec3 pos) {}

    private record Last(UUID emote, int seenTick) {}

    private static final Map<UUID, Mark> MARKS = new HashMap<>();
    /** Последняя эмоция игрока и тик сервера, когда она ещё играла. */
    private static final Map<UUID, Last> LAST = new HashMap<>();
    private static boolean broken;

    public static void onServerTick(TickEvent.ServerTickEvent e) {
        int now = e.getServer().getTickCount();
        if (e.phase != TickEvent.Phase.END || broken || now % 10 != 0) return;
        double max = FixesConfig.EMOTE_STOP_DISTANCE.get();
        boolean resync = FixesConfig.EMOTE_RESYNC.get();
        int interval = Math.max(10, FixesConfig.EMOTE_RESYNC_INTERVAL.get() / 10 * 10); // проверка идёт раз в 10 тиков
        int window = FixesConfig.EMOTE_RESYNC_WINDOW.get() * 20;
        if (max <= 0) MARKS.clear();
        if (!resync) LAST.clear();
        if (max <= 0 && !resync) return;
        Set<UUID> playing = new HashSet<>();
        Set<UUID> online = new HashSet<>();
        try {
            for (ServerPlayer sp : e.getServer().getPlayerList().getPlayers()) {
                UUID id = sp.getUUID();
                online.add(id);
                Pair<KeyframeAnimation, Integer> played = ServerEmoteAPI.getPlayedEmote(id);
                if (played == null) {
                    if (resync) resyncStop(sp, now, interval, window);
                    continue;
                }
                playing.add(id);
                UUID emote = played.getLeft().getUuid();
                if (resync) LAST.put(id, new Last(emote, now));
                if (max <= 0) continue;
                int tick = played.getRight();
                Mark m = MARKS.get(id);
                // Новая эмоция (или та же, начатая заново) — запомнить, где начал.
                if (m == null || !m.emote().equals(emote) || tick < m.tick()) {
                    MARKS.put(id, new Mark(emote, tick, sp.position()));
                    continue;
                }
                MARKS.put(id, new Mark(emote, tick, m.pos()));
                if (sp.position().distanceToSqr(m.pos()) > max * max && !ServerEmoteAPI.isForcedEmote(id)) {
                    ServerEmoteAPI.setPlayerPlayingEmote(id, null);
                    MARKS.remove(id);
                    playing.remove(id);
                }
            }
        } catch (Throwable t) {
            broken = true;
            RpFixes.LOGGER.warn("RP Fixes: сторож эмоций выключен: {}", t.toString());
        }
        MARKS.keySet().retainAll(playing);
        LAST.keySet().retainAll(online);
    }

    /** Эмоции нет: раз в {@code interval} тиков «стоп» последней всем, кто видит игрока, пока не прошло {@code window}. */
    private static void resyncStop(ServerPlayer sp, int now, int interval, int window) {
        Last last = LAST.get(sp.getUUID());
        if (last == null) return;
        if (now - last.seenTick() > window) {
            LAST.remove(sp.getUUID());
            return;
        }
        if (now % interval != 0) return;
        NetData stop = new EmotePacket.Builder().configureToSendStop(last.emote(), sp.getUUID()).build().data;
        ((EmoteServerNetworkAccess) (Object) AbstractServerEmotePlay.getInstance()).rpfixes$sendForEveryoneElse(stop, null, sp);
    }
}
