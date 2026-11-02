package faygolover.rpfixes.fix;

import com.atsuishio.superbwarfare.config.server.MapConfig;
import com.atsuishio.superbwarfare.config.server.SyncConfig;
import com.atsuishio.superbwarfare.item.curio.TacticalTerminalItem;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.world.entity.player.Player;

/** Superb Warfare: положения и «свой/чужой» союзников нужны только тактической карте у того, на ком терминал. */
public final class SbwSync {
    private SbwSync() {}

    /** Карта выключена и дальняя синхронизация тоже — рассылка союзников никому не нужна, весь проход пропускаем. */
    public static boolean skipAll() {
        return FixesConfig.SBW_IFF_NEEDED_ONLY.get() && !MapConfig.ENABLE_TACTICAL_MAP.get() && !SyncConfig.SYNC_ENTITY_OVER_RANGE.get();
    }

    public static boolean wants(Player to) {
        if (!FixesConfig.SBW_IFF_NEEDED_ONLY.get()) return true;
        return MapConfig.ENABLE_TACTICAL_MAP.get() && TacticalTerminalItem.Companion.isTerminalEquipped(to);
    }
}
