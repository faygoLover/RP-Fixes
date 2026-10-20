package faygolover.rpfixes.mixin.effortless;

import faygolover.rpfixes.fix.EffortlessQuiet;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Все сообщения Effortless Building игроку (и с клиента, и с сервера, и переводимые) идут через
 * {@code EffortlessBuilding.log(Player, String, boolean)} — вне творческого режима глушим.
 */
@Mixin(targets = "nl.requios.effortlessbuilding.EffortlessBuilding", remap = false)
public abstract class EffortlessLogMixin {
    @Inject(method = "log(Lnet/minecraft/world/entity/player/Player;Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true, remap = false)
    private static void rpfixes$quiet(Player player, String msg, boolean actionBar, CallbackInfo ci) {
        if (EffortlessQuiet.mute(player)) ci.cancel();
    }
}
