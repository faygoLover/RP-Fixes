package faygolover.rpfixes.mixin.terralith;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.RpFixes;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DebugLevelSource;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldDimensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * Terralith кладёт в датапак своё описание верхнего мира ({@code data/minecraft/dimension/overworld.json}), а
 * {@link WorldDimensions#bake} берёт измерение из датапака раньше выбранного типа мира — суперплоский, «один биом» и
 * отладочный мир превращались в обычный мир Terralith (аудит сборки 16.10). Если выбранный (или записанный в мире)
 * верхний мир — суперплоский, отладочный или «один биом», измерение из датапака для него пропускаем: остаётся выбор
 * игрока. Обычный, «большие биомы» и «расширенный» не трогаем — там Terralith как был.
 */
@Mixin(WorldDimensions.class)
public abstract class WorldDimensionsMixin {

    @ModifyVariable(method = "bake", at = @At("HEAD"), argsOnly = true)
    private Registry<LevelStem> rpfixes$keepChosenOverworld(Registry<LevelStem> fromDatapacks) {
        if (!enabled()) return fromDatapacks;
        LevelStem chosen = ((WorldDimensions) (Object) this).dimensions().get(LevelStem.OVERWORLD);
        if (chosen == null || !special(chosen.generator()) || !fromDatapacks.containsKey(LevelStem.OVERWORLD)) return fromDatapacks;
        if (special(fromDatapacks.get(LevelStem.OVERWORLD).generator())) return fromDatapacks;
        MappedRegistry<LevelStem> copy = new MappedRegistry<>(Registries.LEVEL_STEM, fromDatapacks.registryLifecycle());
        for (var e : fromDatapacks.entrySet()) {
            if (e.getKey().equals(LevelStem.OVERWORLD)) continue;
            copy.register(e.getKey(), e.getValue(), fromDatapacks.lifecycle(e.getValue()));
        }
        RpFixes.LOGGER.info("RP Fixes: верхний мир — выбранный тип ({}), а не из датапака (Terralith)",
                chosen.generator().getClass().getSimpleName());
        return copy.freeze();
    }

    private static boolean special(ChunkGenerator g) {
        return g instanceof FlatLevelSource || g instanceof DebugLevelSource || g.getBiomeSource() instanceof FixedBiomeSource;
    }

    private static boolean enabled() {
        try {
            return FixesConfig.TERRALITH_KEEP_WORLD_TYPE.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }
}
