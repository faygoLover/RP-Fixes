package faygolover.rpfixes.mixin.emotecraft;

import io.github.kosmx.emotes.common.network.GeyserEmotePacket;
import io.github.kosmx.emotes.common.network.objects.NetData;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** Emotecraft 2.2.7, сервер: разослать пакет всем, кто видит игрока (кроме него). */
@Mixin(targets = "io.github.kosmx.emotes.forge.network.ServerNetwork", remap = false)
public interface EmoteServerNetworkAccess {
    @Invoker("sendForEveryoneElse")
    void rpfixes$sendForEveryoneElse(NetData data, GeyserEmotePacket geyser, Player player);
}
