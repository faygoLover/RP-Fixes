package faygolover.rpfixes.mixin.cuffed;

import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import faygolover.rpfixes.fix.CuffedEmotes;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Связанную часть тела эмоция не двигает: остаётся поза Cuffed. */
@Mixin(value = AnimationApplier.class, remap = false)
public abstract class AnimationApplierCuffedMixin {
    @Inject(method = "updatePart(Ljava/lang/String;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("HEAD"), cancellable = true)
    private void rpfixes$keepRestrained(String partName, ModelPart part, CallbackInfo ci) {
        if (CuffedEmotes.locked(partName)) ci.cancel();
    }
}
