package faygolover.rpfixes.mixin.scpo;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.ScpoTicks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SCP: Overtime: «охотник во тьме» ищет только SCP-689, а не все сущности в 25 блоках (см. ScpoTicks.hunter). */
@Mixin(targets = "net.scpo.procedures.ProcedureHunterProcedure", remap = false)
public abstract class ScpoHunterMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At("HEAD"), cancellable = true)
    private static void rpfixes$execute(Event event, LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!FixesConfig.SCPO_TICKS.get()) return;
        ci.cancel();
        ScpoTicks.hunter(world, x, y, z, entity);
    }
}
