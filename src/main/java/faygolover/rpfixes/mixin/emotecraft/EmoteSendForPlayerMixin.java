package faygolover.rpfixes.mixin.emotecraft;

import io.github.kosmx.emotes.common.network.objects.NetData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/**
 * Emotecraft 2.2.7, сервер: когда один игрок начинает видеть другого, который сейчас играет эмоцию, мод шлёт ему эту
 * эмоцию с текущего кадра. При входе новичок начинает видеть остальных раньше, чем попадает в список игроков мира, —
 * мод не находил получателя и падал (NullPointerException в лог при каждом входе), эмоция не доходила. Здесь отправка
 * ждёт получателя: повтор каждый тик, пока он не появится в мире, но не дольше 40 тиков (2 с).
 */
@Mixin(targets = "io.github.kosmx.emotes.forge.network.ServerNetwork", remap = false)
public abstract class EmoteSendForPlayerMixin {
    @Unique private static final int RPFIXES_MAX_WAIT_TICKS = 40;

    @Shadow
    protected abstract void sendForPlayer(NetData data, Player player, UUID target);

    @Inject(method = "sendForPlayer", at = @At("HEAD"), cancellable = true)
    private void rpfixes$waitForTarget(NetData data, Player player, UUID target, CallbackInfo ci) {
        if (player.level().getPlayerByUUID(target) != null) return;
        ci.cancel();
        rpfixes$retry(player.getServer(), data, player, target, RPFIXES_MAX_WAIT_TICKS);
    }

    @Unique
    private void rpfixes$retry(MinecraftServer server, NetData data, Player player, UUID target, int left) {
        if (server == null || left <= 0) return;
        server.tell(new TickTask(server.getTickCount() + 1, () -> {
            if (player.isRemoved()) return;
            if (player.level().getPlayerByUUID(target) != null) sendForPlayer(data, player, target);
            else rpfixes$retry(server, data, player, target, left - 1);
        }));
    }
}
