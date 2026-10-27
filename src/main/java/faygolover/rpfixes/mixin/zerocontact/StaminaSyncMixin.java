package faygolover.rpfixes.mixin.zerocontact;

import faygolover.rpfixes.fix.ZeroContactSync;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * ZeroContact: PlayerStamina.staminaTick шлёт игроку выносливость каждый тик — и когда его выносливость выключена
 * (staminaState = false, у нас своя RP Stamina). Пакет уходит, только если значение или «вкл/выкл» изменились.
 */
@Mixin(targets = "net.zerocontact.network.ModMessages", remap = false)
public abstract class StaminaSyncMixin {
    @Inject(method = "sendToPlayer", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$onlyChanged(Object msg, ServerPlayer player, CallbackInfo ci) {
        if (msg instanceof StaminaPacketAccess s && !ZeroContactSync.changed(player, s.rpfixes$stamina(), s.rpfixes$enabled())) ci.cancel();
    }
}
