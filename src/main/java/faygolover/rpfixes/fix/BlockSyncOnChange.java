package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.WeakHashMap;

/** Butchery, мясорубка: отпечаток последнего отправленного состояния блока (данные блока + состояние). */
public final class BlockSyncOnChange {
    private BlockSyncOnChange() {}

    private static final Map<BlockEntity, Integer> LAST = new WeakHashMap<>();

    public static boolean changed(Level level, BlockPos pos) {
        if (!FixesConfig.BUTCHERY_GRINDER_ON_CHANGE.get() || level.isClientSide) return true;
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return true;
        int now = be.getUpdateTag().hashCode() * 31 + level.getBlockState(pos).hashCode();
        synchronized (LAST) {
            Integer before = LAST.put(be, now);
            return before == null || before != now;
        }
    }
}
