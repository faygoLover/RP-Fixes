package faygolover.rpfixes.mixin.scpo;

import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.ScpoTicks;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SCP: Overtime: выход из команды — ПНВ (команда 966) — проверкой на Java вместо команды каждый тик (см. ScpoTicks). */
@Mixin(targets = "net.scpo.procedures.RemoveNVGProcedure", remap = false)
public abstract class ScpoRemoveNVGMixin {
    @Inject(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$execute(Event event, Entity entity, CallbackInfo ci) {
        if (!FixesConfig.SCPO_TICKS.get()) return;
        ci.cancel();
        ScpoTicks.teamNvg(entity);
    }
}
