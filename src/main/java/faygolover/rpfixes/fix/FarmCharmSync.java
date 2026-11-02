package faygolover.rpfixes.fix;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/** farm_and_charm: сытость животного — при смене и раз в 10 с, только тем, у кого загружен его чанк. */
public final class FarmCharmSync {
    private FarmCharmSync() {}

    private static final Map<Entity, Integer> LAST = new WeakHashMap<>();

    public static boolean changed(Entity animal, int level, int food) {
        if (!FixesConfig.FARMCHARM_SATURATION_ON_CHANGE.get()) return true;
        int now = level * 65536 + food;
        synchronized (LAST) {
            Integer before = LAST.put(animal, now);
            return before == null || before != now || (animal.tickCount + animal.getId()) % 200 == 0;
        }
    }

    public static List<ServerPlayer> watchers(ServerLevel level, Entity animal) {
        if (!FixesConfig.FARMCHARM_SATURATION_ON_CHANGE.get()) return level.players();
        return level.getChunkSource().chunkMap.getPlayers(animal.chunkPosition(), false);
    }
}
