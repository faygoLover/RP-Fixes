package faygolover.rpfixes.mixin.craftedcore;

import faygolover.rpfixes.fix.CraftedCoreSync;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * CraftedCore 5.8.x: PlayerDataSynchronizer.sync зовётся каждый тик каждого игрока (ServerPlayerMixin.serverTick) и
 * шлёт все его данные ему и всем игрокам мира — ~20 пакетов в секунду на пару игроков. Отправка — только если данные
 * отличаются от последних отправленных (см. CraftedCoreSync).
 */
@Mixin(targets = "tocraft.craftedcore.data.PlayerDataSynchronizer", remap = false)
public abstract class PlayerDataSyncMixin {
    @Redirect(method = "syncToSelf", at = @At(value = "INVOKE",
            target = "Ltocraft/craftedcore/network/ModernNetworking;sendToPlayer(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;)V"))
    private static void rpfixes$self(ServerPlayer player, ResourceLocation id, CompoundTag tag) {
        CraftedCoreSync.self(player, id, tag);
    }

    @Redirect(method = "syncToAll", at = @At(value = "INVOKE",
            target = "Ltocraft/craftedcore/network/ModernNetworking;sendToPlayers(Ljava/lang/Iterable;Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/nbt/CompoundTag;)V"))
    private static void rpfixes$all(Iterable<ServerPlayer> players, ResourceLocation id, CompoundTag tag) {
        CraftedCoreSync.all(players, id, tag);
    }
}
