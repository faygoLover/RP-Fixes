package faygolover.rpfixes.mixin.saflashlight;

import com.ogaba.sa_survival.client.flashlight.FlashlightShader;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Survivor's Arsenal (sa_combat) 1.0.0, клиент: экранный круг фонаря умножает уже тёмный кадр на 6,4 и добавляет подъём —
 * в полной тьме цвета взять неоткуда (серое), светлое упирается в белый (люди белые). Когда под ним светит настоящий
 * свет фонаря FL Render ({@code FlRender.flashlightLight()}: конус освещает блоки при рисовании, сущности — как у факела),
 * круг только подчёркивает пятно: своя копия шейдера мода ({@code rpfixes:sa_flash}) с uniform {@code FlCone} —
 * усиление 0,55 вместо 6,4, без подъёма, мягкий потолок. Без FL Render и с шейдерпаком — всё как у мода.
 */
@Mixin(value = FlashlightShader.class, remap = false)
public abstract class FlashlightShaderMixin {
    private static final ResourceLocation RPFIXES_CHAIN = new ResourceLocation("rpfixes", "shaders/post/sa_flashlight.json");

    @Shadow
    private static List<PostPass> passes;

    @Redirect(method = "ensureChain", at = @At(value = "FIELD", opcode = org.objectweb.asm.Opcodes.GETSTATIC,
            target = "Lcom/ogaba/sa_survival/client/flashlight/FlashlightShader;CHAIN:Lnet/minecraft/resources/ResourceLocation;"))
    private static ResourceLocation rpfixes$chain() {
        return RPFIXES_CHAIN;
    }

    @Inject(method = "upload", at = @At("TAIL"))
    private static void rpfixes$cone(CallbackInfo ci) {
        List<PostPass> current = passes;
        if (current == null) return;
        float cone = enabled() && faygolover.rpfixes.compat.FlRenderCompat.flashlightLight() ? 1.0F : 0.0F;
        for (PostPass pass : current) pass.getEffect().safeGetUniform("FlCone").set(cone);
    }

    private static boolean enabled() {
        try {
            return FixesConfig.SA_FLASHLIGHT_SOFT.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }
}
