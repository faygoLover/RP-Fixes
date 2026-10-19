package faygolover.rpfixes.mixin.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import faygolover.rpfixes.fix.EmoteLegsDebug;
import net.minecraft.client.model.AgeableListModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Ноги в момент отрисовки — для {@link EmoteLegsDebug}. */
@Mixin(AgeableListModel.class)
public abstract class AgeableListModelLegsDebugMixin {
    @Inject(method = "renderToBuffer", at = @At("HEAD"))
    private void rpfixes$legsAtRender(PoseStack pose, VertexConsumer buffer, int light, int overlay, float r, float g, float b, float a, CallbackInfo ci) {
        EmoteLegsDebug.beforeRender(this);
    }
}
