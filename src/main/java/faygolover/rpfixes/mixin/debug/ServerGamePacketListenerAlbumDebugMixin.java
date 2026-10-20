package faygolover.rpfixes.mixin.debug;

import faygolover.rpfixes.fix.AlbumDebug;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Серверная сторона кликов в меню — для {@link AlbumDebug} (RETURN — только на потоке сервера, после обработки). */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerAlbumDebugMixin {
    @Shadow public ServerPlayer player;

    @Inject(method = "handleContainerButtonClick", at = @At("RETURN"))
    private void rpfixes$albumButton(ServerboundContainerButtonClickPacket p, CallbackInfo ci) {
        AlbumDebug.serverButton(player, p.getContainerId(), p.getButtonId());
    }

    @Inject(method = "handleContainerClick", at = @At("RETURN"))
    private void rpfixes$albumClick(ServerboundContainerClickPacket p, CallbackInfo ci) {
        AlbumDebug.serverClick(player, p.getContainerId(), p.getSlotNum(), p.getButtonNum(), p.getClickType());
    }
}
