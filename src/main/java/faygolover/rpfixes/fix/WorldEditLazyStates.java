package faygolover.rpfixes.fix;

import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.internal.block.BlockStateIdAccess;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.registry.BlockRegistry;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.world.level.block.Block;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Ленивые состояния блоков WorldEdit. Без регистрации ID WorldEdit всё равно работает (ForgeAdapter переводит состояние
 * на лету через ForgeTransmogrifier), регистрация лишь ускоряет перевод. Поэтому регистрируем только те типы блоков,
 * которых WorldEdit реально коснулся: память — по факту использования, а не на все блоки сборки сразу.
 */
public final class WorldEditLazyStates {
    private WorldEditLazyStates() {}

    private static volatile BlockRegistry registry;

    /** Вместо Capability.WORLD_EDITING.ready(): запомнить реестр и дорегистрировать уже построенные типы. */
    public static synchronized void ready(Platform platform) {
        registry = platform.getRegistries().getBlockRegistry();
        presize();
        int types = 0, states = 0;
        for (BlockType type : BlockType.REGISTRY.values()) {
            Collection<BlockState> built = builtStates(type);
            if (built == null) continue;
            types++;
            states += register(built);
        }
        RpFixes.LOGGER.info("RP Fixes: WorldEdit — состояния блоков лениво (вместо всех {} сразу); уже построено типов: {}, состояний: {}",
                Block.BLOCK_STATE_REGISTRY.size(), types, states);
    }

    /** Тип блока только что построил свои состояния. */
    public static void onGenerated(Collection<BlockState> states) {
        if (registry == null || !FixesConfig.WORLDEDIT_LAZY_STATES.get()) return; // до старта сервера — дорегистрирует ready()
        synchronized (WorldEditLazyStates.class) {
            register(states);
        }
    }

    private static int register(Collection<BlockState> states) {
        int n = 0;
        for (BlockState state : states) {
            if (BlockStateIdAccess.isValidInternalId(BlockStateIdAccess.getBlockStateId(state))) continue;
            BlockStateIdAccess.register(state, registry.getInternalBlockStateId(state).orElse(BlockStateIdAccess.invalidId()));
            n++;
        }
        return n;
    }

    // ---- рефлексия по внутренностям WorldEdit 7.2.x; при несовпадении — просто без этих оптимизаций ----

    private static Field statesMapField, lazyValueField;

    /** Состояния типа, если WorldEdit их уже построил (LazyReference посчитан), иначе null — не трогаем, чтобы не строить. */
    @SuppressWarnings("unchecked")
    private static Collection<BlockState> builtStates(BlockType type) {
        try {
            if (statesMapField == null) {
                statesMapField = BlockType.class.getDeclaredField("blockStatesMap");
                statesMapField.setAccessible(true);
            }
            Object lazy = statesMapField.get(type);
            if (lazyValueField == null) {
                lazyValueField = lazy.getClass().getDeclaredField("value");
                lazyValueField.setAccessible(true);
            }
            Object v = lazyValueField.get(lazy);
            return v instanceof Map<?, ?> m ? (Collection<BlockState>) m.values() : null;
        } catch (ReflectiveOperationException | RuntimeException e) {
            return null;
        }
    }

    /** Таблица id → состояние сразу под все ID игры: при ленивой регистрации она не перестраивается на ходу. */
    private static void presize() {
        try {
            Field f = BlockStateIdAccess.class.getDeclaredField("TO_STATE");
            f.setAccessible(true);
            Object map = f.get(null);
            map.getClass().getMethod("ensureCapacity", int.class).invoke(map, Block.BLOCK_STATE_REGISTRY.size());
        } catch (ReflectiveOperationException | RuntimeException e) {
            RpFixes.LOGGER.debug("RP Fixes: WorldEdit TO_STATE presize skipped: {}", e.toString());
        }
    }
}
