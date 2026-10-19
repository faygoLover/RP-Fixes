package faygolover.rpfixes.mixin.debug;

import faygolover.rpfixes.fix.SkinDebug;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Пакеты списка игроков про себя — для {@link SkinDebug}. */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerSkinDebugMixin {
    @Inject(method = "handlePlayerInfoUpdate", at = @At("HEAD"))
    private void rpfixes$skinUpdate(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        SkinDebug.onUpdate(packet);
    }

    @Inject(method = "handlePlayerInfoRemove", at = @At("HEAD"))
    private void rpfixes$skinRemove(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        SkinDebug.onRemove(packet);
    }
}
