package faygolover.rpfixes.mixin.scpo;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.ScpoTicks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SCP: Overtime: function scpo:tick (шесть @e по всем мирам) — только когда такие SCP есть на сервере. */
@Mixin(targets = "net.scpo.procedures.MobTickSculptureProcedure", remap = false)
public abstract class ScpoSculptureTickMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;)V", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$execute(Event event, LevelAccessor world, CallbackInfo ci) {
        if (FixesConfig.SCPO_TICKS.get() && !ScpoTicks.sculptureTickNeeded(world)) ci.cancel();
    }
}
