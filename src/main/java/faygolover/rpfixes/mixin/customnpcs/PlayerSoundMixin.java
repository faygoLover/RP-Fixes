package faygolover.rpfixes.mixin.customnpcs;

import faygolover.rpfixes.FixesConfig;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Custom NPCs, клиент: каждый звук, который играет у игрока (шаги, удары, музыка), уходил серверу SPacketPlayerSound —
 * ради скриптового события игрока playSound (~60 пакетов/с от каждого). На S5 скриптов игрока нет — не шлём.
 */
@Pseudo
@Mixin(targets = "noppes.npcs.client.ClientEventHandler", remap = false)
public abstract class PlayerSoundMixin {
    @Inject(method = "cnpcPlaySound(Lnet/minecraftforge/client/event/sound/PlaySoundEvent;)V", at = @At("HEAD"), cancellable = true)
    private void rpfixes$noSoundSpam(PlaySoundEvent event, CallbackInfo ci) {
        if (FixesConfig.CNPC_NO_PLAYER_SOUNDS.get()) ci.cancel();
    }
}
