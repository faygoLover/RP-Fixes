package faygolover.rpfixes.mixin.sbw;

import com.atsuishio.superbwarfare.tools.MinecraftUtil;
import faygolover.rpfixes.fix.SbwSync;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Superb Warfare, IffItem.onServerTick: каждый тик каждому игроку про каждого из той же команды — PlayerInfoSync и
 * EntityRelationSync (N² пакетов, 615 КБ/с при 16 игроках на живом 31.10). На клиенте их читает только тактическая карта:
 * она включается {@code enable_tactical_map} и открывается терминалом в слоте Curios. Шлём только тому, у кого карта может
 * быть открыта; карта выключена — проход пропускается целиком (кроме случая, когда включена дальняя синхронизация: ей нужна
 * регистрация игроков из этого прохода).
 */
@Mixin(targets = "com.atsuishio.superbwarfare.item.curio.IffItem$Companion", remap = false)
public abstract class IffSyncMixin {
    @Inject(method = "onServerTick", at = @At("HEAD"), cancellable = true)
    private void rpfixes$nobodyNeeds(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (SbwSync.skipAll()) ci.cancel();
    }

    @Redirect(method = "onServerTick", at = @At(value = "INVOKE",
            target = "Lcom/atsuishio/superbwarfare/tools/MinecraftUtil;sendPacketTo(Lnet/minecraft/world/entity/player/Player;Ljava/lang/Object;)V"))
    private void rpfixes$onlyWithTerminal(Player to, Object msg) {
        if (SbwSync.wants(to)) MinecraftUtil.sendPacketTo(to, msg);
    }
}
