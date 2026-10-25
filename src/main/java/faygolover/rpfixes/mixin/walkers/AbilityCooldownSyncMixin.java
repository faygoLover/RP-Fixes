package faygolover.rpfixes.mixin.walkers;

import faygolover.rpfixes.fix.WalkersCooldownSync;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Walkers 5.8.x: откат способности шлётся игроку каждый тик (PlayerEntityTickMixin) — теперь только когда он изменился. */
@Mixin(targets = "tocraft.walkers.api.PlayerAbilities", remap = false)
public abstract class AbilityCooldownSyncMixin {
    @Inject(method = "sync(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$onlyChanged(ServerPlayer player, CallbackInfo ci) {
        if (!WalkersCooldownSync.changed(player)) ci.cancel();
    }
}
