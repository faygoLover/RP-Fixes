package faygolover.rpfixes.mixin.debug;

import faygolover.rpfixes.fix.EmoteLegsDebug;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Ноги сразу после позы — для {@link EmoteLegsDebug}. */
@Mixin(value = PlayerModel.class, priority = 3000)
public abstract class PlayerModelLegsDebugMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    private void rpfixes$legsAfterSetup(T entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, CallbackInfo ci) {
        EmoteLegsDebug.afterSetupAnim((PlayerModel<?>) (Object) this, entity);
    }
}
