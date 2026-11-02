package faygolover.rpfixes.mixin.clanginghowl;

import com.mongoose.clanginghowl.common.capabilities.CHCapHelper;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Clanging Howl, CHCapHelper.setTicksInAir: пока игрок не на земле (прыжок, падение, сидит на стуле, полёт ГМа), сервер
 * каждый тик слал всю капу ему и всем рядом (436/с на живом 31.10). Счётчик «в воздухе» клиент считает сам тем же событием тика.
 */
@Mixin(targets = "com.mongoose.clanginghowl.common.capabilities.CHCapHelper", remap = false)
public abstract class AirTicksSyncMixin {
    @Redirect(method = "setTicksInAir", at = @At(value = "INVOKE",
            target = "Lcom/mongoose/clanginghowl/common/capabilities/CHCapHelper;sendCHCapUpdatePacket(Lnet/minecraft/world/entity/LivingEntity;)V"))
    private static void rpfixes$noAirSync(LivingEntity entity) {
        if (!FixesConfig.CLANGINGHOWL_QUIET.get()) CHCapHelper.sendCHCapUpdatePacket(entity);
    }
}
