package faygolover.rpfixes.mixin.zerocontact;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** ZeroContact: содержимое пакета выносливости — для сравнения с последним отправленным. */
@Mixin(targets = "net.zerocontact.network.NetworkHandler$SyncStaminaPacket", remap = false)
public interface StaminaPacketAccess {
    @Accessor("stamina") float rpfixes$stamina();
    @Accessor("enabled") boolean rpfixes$enabled();
}
