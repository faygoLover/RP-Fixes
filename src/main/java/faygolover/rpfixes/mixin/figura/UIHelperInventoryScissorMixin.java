package faygolover.rpfixes.mixin.figura;

import faygolover.rpfixes.fix.FiguraInventoryScissor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import org.figuramc.figura.model.rendering.EntityRenderMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Figura: рамка игрока в инвентаре выживания — как у ванилы — см. {@link FiguraInventoryScissor}. */
@Pseudo
@Mixin(targets = "org.figuramc.figura.utils.ui.UIHelper", remap = false)
public abstract class UIHelperInventoryScissorMixin {
    @Inject(method = "drawEntity", at = @At("HEAD"))
    private static void rpfixes$vanillaScissor(float x, float y, float scale, float pitch, float yaw, LivingEntity entity,
                                               GuiGraphics gui, EntityRenderMode mode, CallbackInfo ci) {
        FiguraInventoryScissor.fix(x, y, scale, gui, mode);
    }
}
