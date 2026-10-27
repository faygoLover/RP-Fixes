package faygolover.rpfixes.fix;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.util.Pair;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import io.github.kosmx.emotes.api.events.server.ServerEmoteAPI;
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
 */
public final class EmoteStale {
    private EmoteStale() {}

    private record Mark(UUID emote, int tick, Vec3 pos) {}

    private static final Map<UUID, Mark> MARKS = new HashMap<>();
    private static boolean broken;

    public static void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || broken || e.getServer().getTickCount() % 10 != 0) return;
        double max = FixesConfig.EMOTE_STOP_DISTANCE.get();
        if (max <= 0) {
            MARKS.clear();
            return;
        }
        Set<UUID> playing = new HashSet<>();
        try {
            for (ServerPlayer sp : e.getServer().getPlayerList().getPlayers()) {
                UUID id = sp.getUUID();
                Pair<KeyframeAnimation, Integer> played = ServerEmoteAPI.getPlayedEmote(id);
                if (played == null) continue;
                playing.add(id);
                UUID emote = played.getLeft().getUuid();
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
    }
}
