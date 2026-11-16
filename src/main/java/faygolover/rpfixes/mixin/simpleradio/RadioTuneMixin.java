package faygolover.rpfixes.mixin.simpleradio;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Simple Radio: «Технофоб» (RP Perks) слушает и говорит, но частоту и модуляцию рации не меняет — сервер пакет пропускает. */
@Pseudo
@Mixin(targets = "com.codinglitch.simpleradio.core.networking.SimpleRadioNetworking", remap = false)
public abstract class RadioTuneMixin {
    @Inject(method = "handleRadioUpdate", at = @At("HEAD"), cancellable = true, require = 0, remap = false)
    private static void rpfixes$technophobe(@Coerce Object packet, MinecraftServer server, ServerPlayer player, CallbackInfo ci) {
        if (player != null && faygolover.rpfixes.s5.PerkLocks.denyRadioTune(player)) ci.cancel();
    }
}
