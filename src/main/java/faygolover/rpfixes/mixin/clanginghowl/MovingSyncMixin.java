package faygolover.rpfixes.mixin.clanginghowl;

import com.mongoose.clanginghowl.common.capabilities.CHCapHelper;
import com.mongoose.clanginghowl.common.network.CHNetwork;
import com.mongoose.clanginghowl.utils.MobUtil;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.ClangingHowlClient;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Clanging Howl, CHEvents.TickEvent: клиент каждый тик для КАЖДОЙ видимой живой сущности слал серверу CIsMovingPacket
 * (~67/с от игрока). Сервер берёт «двигается» только для эффектов мода. Теперь клиент шлёт только про себя и при смене,
 * а мобам с эффектами сервер считает это сам (движение мобов и так серверное).
 */
@Mixin(targets = "com.mongoose.clanginghowl.common.events.CHEvents", remap = false)
public abstract class MovingSyncMixin {
    @Redirect(method = "TickEvent", at = @At(value = "INVOKE",
            target = "Lcom/mongoose/clanginghowl/common/network/CHNetwork;sendToServer(Ljava/lang/Object;)V"))
    private static void rpfixes$ownOnChange(Object msg, LivingEvent.LivingTickEvent event) {
        if (!FixesConfig.CLANGINGHOWL_QUIET.get() || ClangingHowlClient.shouldSend(event.getEntity())) CHNetwork.sendToServer(msg);
    }

    @Inject(method = "TickEvent", at = @At("HEAD"))
    private static void rpfixes$mobsOnServer(LivingEvent.LivingTickEvent event, CallbackInfo ci) {
        LivingEntity e = event.getEntity();
        if (e.level().isClientSide || e instanceof Player || e.getActiveEffects().isEmpty() || !FixesConfig.CLANGINGHOWL_QUIET.get()) return;
        CHCapHelper.setMoving(e, MobUtil.isMoving(e));
    }
}
