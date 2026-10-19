package faygolover.rpfixes.mixin.cuffed;

import faygolover.rpfixes.fix.CuffedEmotes;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Запомнить, чью модель сейчас ставят — для {@link AnimationApplierCuffedMixin}. */
@Mixin(PlayerModel.class)
public abstract class PlayerModelCuffedMixin<T extends LivingEntity> {
    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("HEAD"))
    private void rpfixes$rememberPlayer(T entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, CallbackInfo ci) {
        CuffedEmotes.CURRENT.set(entity instanceof Player p ? p : null);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("RETURN"))
    private void rpfixes$forgetPlayer(T entity, float limbSwing, float limbSwingAmount, float age, float yaw, float pitch, CallbackInfo ci) {
        CuffedEmotes.CURRENT.remove();
    }
}
