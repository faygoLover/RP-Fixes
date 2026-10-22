package faygolover.rpfixes.mixin.breaching;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

/**
 * TaCZ Tactical Breaching 1.0.5: фонарик на дубинке (Nightstick) светит настоящими блоками {@code minecraft:light}.
 * <ul>
 *   <li>При входе игрока {@code cleanupNearbyStrayLights} стирал ВСЕ блоки света в радиусе
 *       {@code max(10, flashlightRange + 4)} = 22 блока (±4 по высоте) — расставленный строителями свет пропадал
 *       вокруг каждого входящего. Здесь это отключено: свои блоки мод и так убирает по списку {@code PLACED_LIGHTS}.</li>
 *   <li>Луч проходил и по чужим блокам света: перезаписывал их уровень, записывал в свои и убирал, когда луч уходил.
 *       Теперь чужой блок света для луча — как стена, а убирать мод может только записанные им самим.</li>
 * </ul>
 * Цель есть только в собранной сборке (имена SRG), поэтому имена Minecraft здесь записаны как SRG и {@code remap = false}.
 */
@Mixin(targets = "com.victoriomods.taczballisticbreaching.NightstickFlashlightLightHandler", remap = false)
public abstract class NightstickLightsMixin {

    @Shadow @Final private static Set<BlockPos> PLACED_LIGHTS;

    @Inject(method = "cleanupNearbyStrayLights", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$noRadiusSweep(ServerPlayer player, CallbackInfo ci) {
        if (enabled()) ci.cancel();
    }

    @Inject(method = "removeIfUnused", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$removeOnlyOwn(ServerLevel level, UUID owner, BlockPos pos, CallbackInfo ci) {
        if (enabled() && !PLACED_LIGHTS.contains(pos)) ci.cancel();
    }

    @Redirect(method = "updateLights", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerLevel;m_8055_(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private static BlockState rpfixes$foreignLightIsWall(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (enabled() && state.is(Blocks.LIGHT) && !PLACED_LIGHTS.contains(pos)) return Blocks.STONE.defaultBlockState();
        return state;
    }

    private static boolean enabled() {
        try {
            return FixesConfig.BREACHING_KEEP_LIGHTS.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }
}
