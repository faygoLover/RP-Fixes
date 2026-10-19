package faygolover.rpfixes.mixin.bendy;

import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import faygolover.rpfixes.fix.LazyBends;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Запомнить, какую часть тела сейчас ставит эмоция — чтобы знать, куда гнуть (см. BendHelperMixin). */
@Mixin(value = AnimationApplier.class, remap = false)
public abstract class AnimationApplierBendMixin {
    @Inject(method = "updatePart(Ljava/lang/String;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("HEAD"))
    private void rpfixes$partStart(String partName, ModelPart part, CallbackInfo ci) {
        LazyBends.PART.set(partName);
    }

    @Inject(method = "updatePart(Ljava/lang/String;Lnet/minecraft/client/model/geom/ModelPart;)V", at = @At("RETURN"))
    private void rpfixes$partEnd(String partName, ModelPart part, CallbackInfo ci) {
        LazyBends.PART.remove();
    }
}
