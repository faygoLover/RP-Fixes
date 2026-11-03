package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import io.github.kosmx.emotes.forge.network.ClientNetworkInstance;
import io.github.kosmx.emotes.forge.network.ServerNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;

/**
 * Emotecraft, клиент: свои настройки клиент шлёт серверу, только когда до него дошли оба события — вход (LoggingIn) и
 * регистрация канала. Потерялось одно — настроек нет, сервер не отвечает флагом «эмоции веду я», и клиент до конца сессии
 * сам повторяет свою эмоцию каждому новому игроку рядом (на живом сервер это отбрасывает: «does not respect server-side
 * emote tracking», почти у всех игроков). Здесь: после входа, раз в 5 с и не больше 5 раз, пока флага нет, а канал у
 * сервера есть, — настройки уходят ещё раз. Повторная отправка безвредна.
 */
public final class EmoteConfigRetry {
    private EmoteConfigRetry() {}

    private static final int FIRST = 40, STEP = 100, TRIES = 5;
    private static int ticks = -1;

    public static void onLogin(ClientPlayerNetworkEvent.LoggingIn e) { ticks = 0; }

    public static void onLogout(ClientPlayerNetworkEvent.LoggingOut e) { ticks = -1; }

    public static void onClientTick(TickEvent.ClientTickEvent e) {
        if (e.phase != TickEvent.Phase.END || ticks < 0) return;
        int t = ++ticks;
        if (t < FIRST || (t - FIRST) % STEP != 0) return;
        if (t > FIRST + STEP * (TRIES - 1)) { ticks = -1; return; }
        if (!FixesConfig.EMOTE_CONFIG_RETRY.get() || !serverHasChannel()) return;
        ClientNetworkInstance net = ClientNetworkInstance.networkInstance;
        if (net.isServerTrackingPlayState()) { ticks = -1; return; }
        net.sendConfigCallback();
    }

    public static boolean serverHasChannel() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null && ServerNetwork.channel.isRemotePresent(connection.getConnection());
    }
}
