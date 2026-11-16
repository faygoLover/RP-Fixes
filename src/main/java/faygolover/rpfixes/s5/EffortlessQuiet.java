package faygolover.rpfixes.s5;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.world.entity.player.Player;

/**
 * Effortless Building у игроков (20.10.2026): в survival он и так выключен ({@code allowInSurvival=false}), но писал в
 * чат на Alt, Ctrl+Z и т. п. и занимал клавиши. Убрать мод только у игроков нельзя — он регистрирует предметы, и
 * Forge не пустит на сервер без него. Поэтому: вне творческого режима его сообщения не показываются, а в сборке
 * игрока (без WorldEdit — он есть только у админов) все клавиши EB при запуске снимаются.
 */
public final class EffortlessQuiet {
    private EffortlessQuiet() {}

    public static boolean mute(Player player) {
        try {
            return player != null && !player.isCreative() && FixesConfig.EFFORTLESS_QUIET.get();
        } catch (IllegalStateException notLoaded) {
            return player != null && !player.isCreative();
        }
    }
}
