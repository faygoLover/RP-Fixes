package faygolover.rpfixes.mixin.emotecraft;

import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.EmoteConfigRetry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Emotecraft, клиент: ClientEmotePlay.clientRepeatLocalEmote — когда свой игрок играет эмоцию и рядом появляется другой,
 * клиент сам шлёт ему эмоцию, если думает, что сервер эмоции не ведёт. Сервер с Emotecraft ведёт их всегда и сам рассылает
 * идущую эмоцию тем, кто начал видеть игрока; повтор он отбрасывает со строкой в логе. Есть канал Emotecraft у сервера —
 * повтор не шлём (страховка к {@link EmoteConfigRetry}).
 */
@Mixin(targets = "io.github.kosmx.emotes.main.network.ClientEmotePlay", remap = false)
public abstract class ClientRepeatMixin {
    @Inject(method = "clientRepeatLocalEmote", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$serverTracks(KeyframeAnimation emote, int tick, UUID target, CallbackInfo ci) {
        if (FixesConfig.EMOTE_NO_CLIENT_REPEAT.get() && EmoteConfigRetry.serverHasChannel()) ci.cancel();
    }
}
