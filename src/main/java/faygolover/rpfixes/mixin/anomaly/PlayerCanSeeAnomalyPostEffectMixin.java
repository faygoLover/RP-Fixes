package faygolover.rpfixes.mixin.anomaly;

import faygolover.rpfixes.fix.AnomalyPostEffect;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Anomaly Rephased снимает только свой постэффект — см. {@link AnomalyPostEffect}. */
@Pseudo
@Mixin(targets = "net.mcreator.anomalyrephased.procedures.PlayerCanSeeAnomalyProcedure", remap = false)
public abstract class PlayerCanSeeAnomalyPostEffectMixin {
    @Redirect(method = "execute(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraft/world/level/LevelAccessor;DDDLnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownEffect()V", remap = true))
    private static void rpfixes$onlyOwnPostEffect(GameRenderer renderer) {
        AnomalyPostEffect.shutdown(renderer);
    }
}
