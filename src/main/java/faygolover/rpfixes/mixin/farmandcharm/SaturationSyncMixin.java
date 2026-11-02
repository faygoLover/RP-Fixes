package faygolover.rpfixes.mixin.farmandcharm;

import faygolover.rpfixes.fix.FarmCharmSync;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * farm_and_charm, PacketHandler.sendSaturationSync: каждая корова, овца, свинья и курица каждый тик слала свою сытость
 * ВСЕМ игрокам измерения (животные × игроки × 20; всплески у ферм). Теперь — при смене и раз в 10 с, и только тем,
 * у кого загружен чанк животного.
 */
@Mixin(targets = "net.satisfy.farm_and_charm.core.network.PacketHandler", remap = false)
public abstract class SaturationSyncMixin {
    @Inject(method = "sendSaturationSync", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$onChange(SyncSaturationPacket packet, Entity entity, CallbackInfo ci) {
        if (!FarmCharmSync.changed(entity, packet.getLevel(), packet.getFoodCounter())) ci.cancel();
    }

    @Redirect(method = "sendSaturationSync", at = @At(value = "INVOKE", remap = true,
            target = "Lnet/minecraft/server/level/ServerLevel;players()Ljava/util/List;"))
    private static List<ServerPlayer> rpfixes$watchers(ServerLevel level, SyncSaturationPacket packet, Entity entity) {
        return FarmCharmSync.watchers(level, entity);
    }
}
