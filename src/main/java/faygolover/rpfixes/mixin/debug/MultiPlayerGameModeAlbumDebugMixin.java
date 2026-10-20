package faygolover.rpfixes.mixin.debug;

import faygolover.rpfixes.fix.AlbumDebug;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Клиентская сторона кликов в меню — для {@link AlbumDebug}. */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeAlbumDebugMixin {
    @Inject(method = "handleInventoryButtonClick", at = @At("HEAD"))
    private void rpfixes$albumButton(int containerId, int buttonId, CallbackInfo ci) {
        AlbumDebug.clientButton(Minecraft.getInstance().player, containerId, buttonId);
    }

    @Inject(method = "handleInventoryMouseClick", at = @At("HEAD"))
    private void rpfixes$albumClick(int containerId, int slotId, int button, ClickType type, Player player, CallbackInfo ci) {
        AlbumDebug.clientClick(player, containerId, slotId, button, type);
    }
}
