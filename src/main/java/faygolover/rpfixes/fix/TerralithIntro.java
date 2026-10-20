package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.fml.ModList;

/**
 * Terralith при первом запуске мира пишет всем в чат рекламу (Discord, хостинг): функция {@code terralith:setup} из
 * тега {@code minecraft:load} ставит {@code terralith:toast}, если у {@code %USED} в {@code tr.first} меньше 2.
 * Функции загрузки идут на первом тике сервера, а сервер уже запущен раньше — здесь отметка «уже показано» ставится
 * заранее, и сообщение не появляется.
 */
public final class TerralithIntro {
    private TerralithIntro() {}

    public static void onServerStarted(ServerStartedEvent e) {
        if (!ModList.get().isLoaded("terralith") || !FixesConfig.TERRALITH_NO_INTRO.get()) return;
        ServerScoreboard board = e.getServer().getScoreboard();
        Objective obj = board.getObjective("tr.first");
        if (obj == null) obj = board.addObjective("tr.first", ObjectiveCriteria.DUMMY, Component.literal("tr.first"), ObjectiveCriteria.RenderType.INTEGER);
        var score = board.getOrCreatePlayerScore("%USED", obj);
        if (score.getScore() < 2) score.setScore(2);
    }
}
