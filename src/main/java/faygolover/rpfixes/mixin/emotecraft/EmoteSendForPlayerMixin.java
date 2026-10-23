package faygolover.rpfixes.mixin.emotecraft;

import io.github.kosmx.emotes.common.network.objects.NetData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Emotecraft 2.2.7, сервер: при входе игрока соседи начинают его «видеть» раньше, чем он попал в список игроков мира, и
 * мод шлёт эмоцию игроку null — NullPointerException со стеком в лог при каждом входе. Нет получателя — нечего слать.
 */
@Mixin(targets = "io.github.kosmx.emotes.forge.network.ServerNetwork", remap = false)
public abstract class EmoteSendForPlayerMixin {

    @Inject(method = "sendForPlayer", at = @At("HEAD"), cancellable = true)
    private void rpfixes$noTarget(NetData data, Player player, UUID target, CallbackInfo ci) {
        if (player.level().getPlayerByUUID(target) == null) ci.cancel();
    }
}
