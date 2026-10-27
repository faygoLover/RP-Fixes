package faygolover.rpfixes.mixin.mcreator;

import faygolover.rpfixes.FixesConfig;
import net.mcreator.mekanismexplosives.network.MekanismexplosivesModVariables;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mekanism Explosives: TimersProcedure в каждом измерении каждый тик прибавляет GlobalTimer и рассылает все переменные
 * мода всем игрокам (SavedDataSyncMessage, ~110 пакетов в секунду на игрока при трёх измерениях). Таймер считается
 * как раньше, но только у себя: у клиента он и так тикает сам (то же событие тика мира), рассылки нет.
 */
@Mixin(targets = "net.mcreator.mekanismexplosives.procedures.TimersProcedure", remap = false)
public abstract class MekTimersMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$noBroadcast(Event event, LevelAccessor world, CallbackInfo ci) {
        if (!FixesConfig.MEK_EXPLOSIVES_TIMER_LOCAL.get()) return;
        ci.cancel();
        MekanismexplosivesModVariables.MapVariables v = MekanismexplosivesModVariables.MapVariables.get(world);
        v.GlobalTimer += 0.05;
        if (v.GlobalTimer >= 1.1) v.GlobalTimer = 0.0;
        v.setDirty();
    }
}
