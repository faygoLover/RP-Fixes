package faygolover.rpfixes.mixin.securitycraft;

import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * SecurityCraft, камера в рамке (frame feed): пока он снимает вид с камеры, туман должен идти по дальности обзора рамки.
 * Его собственная вставка (LevelRenderer.renderLevel, локальная переменная у FogRenderer.setupColor) не встаёт —
 * это место переписано Embeddium/Oculus. Здесь то же на входе в FogRenderer.setupFog для тумана ландшафта.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererFrameFeedMixin {
    private static MethodHandle rpfixes$capturing, rpfixes$viewDistance;
    private static boolean rpfixes$ready, rpfixes$broken;

    @ModifyVariable(method = "setupFog", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static float rpfixes$frameFeedFog(float farPlane, Camera camera, FogRenderer.FogMode mode) {
        if (mode != FogRenderer.FogMode.FOG_TERRAIN || !FixesConfig.SECURITYCRAFT_FRAME_FOG.get() || !rpfixes$init()) return farPlane;
        try {
            if ((boolean) rpfixes$capturing.invoke()) return Math.max((int) rpfixes$viewDistance.invoke((Object) null) * 16, 32);
        } catch (Throwable t) {
            rpfixes$broken = true;
        }
        return farPlane;
    }

    private static boolean rpfixes$init() {
        if (rpfixes$ready || rpfixes$broken) return rpfixes$ready;
        try {
            Class<?> h = Class.forName("net.geforcemods.securitycraft.entity.camera.FrameFeedHandler");
            Class<?> frame = Class.forName("net.geforcemods.securitycraft.blockentities.FrameBlockEntity");
            MethodHandles.Lookup l = MethodHandles.publicLookup();
            rpfixes$capturing = l.findStatic(h, "isCapturingCamera", MethodType.methodType(boolean.class));
            rpfixes$viewDistance = l.findStatic(h, "getFrameFeedViewDistance", MethodType.methodType(int.class, frame));
            rpfixes$ready = true;
        } catch (ReflectiveOperationException | RuntimeException e) {
            rpfixes$broken = true;
        }
        return rpfixes$ready;
    }
}
