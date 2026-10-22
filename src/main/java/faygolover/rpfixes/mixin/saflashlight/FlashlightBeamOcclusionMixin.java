package faygolover.rpfixes.mixin.saflashlight;

import com.ogaba.sa_survival.client.flashlight.FlashlightBeam;
import faygolover.rpfixes.FixesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Survivor's Arsenal (sa_combat) 1.0.0, клиент: фонарь светит экранным эффектом — осветляет каждый видимый пиксель в
 * своём конусе (до 38 блоков), стен между фонарём и поверхностью он не знает. Свой фонарь от первого лица этим не
 * страдает (камера почти в фонаре), а чужой из соседней комнаты и свой в виде от 3-го лица светили сквозь стены.
 * <ul>
 *   <li>Фонарь, которого камера не видит (между камерой и фонарём блок; стекло не мешает), не рисуется.</li>
 *   <li>Дальность луча обрезается по блоку, в который упирается его ось: затухание начинается у этой стены, и за ней
 *       свет быстро гаснет, а не идёт на все 38 блоков.</li>
 * </ul>
 * Свой фонарь от первого лица не меняется.
 */
@Mixin(value = FlashlightBeam.class, remap = false)
public abstract class FlashlightBeamOcclusionMixin {
    /** У мода затухание идёт с 0,62 дальности до полной ({@code flash.fsh}): стена должна попасть в начало затухания. */
    private static final float FADE_START = 0.62F;

    @Inject(method = "resolve", at = @At("RETURN"), cancellable = true)
    private static void rpfixes$occlude(Player player, boolean firstPerson, float partialTick, CallbackInfoReturnable<FlashlightBeam> cir) {
        FlashlightBeam beam = cir.getReturnValue();
        if (beam == null || firstPerson || !enabled()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        // Камера не видит фонарь — луча для неё нет.
        BlockHitResult toLamp = mc.level.clip(new ClipContext(camera, beam.origin, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, mc.player));
        if (toLamp.getType() == HitResult.Type.BLOCK && toLamp.getLocation().distanceTo(camera) < camera.distanceTo(beam.origin) - 0.25) {
            cir.setReturnValue(null);
            return;
        }
        // Ось луча упёрлась в блок — дальше стены свет быстро гаснет.
        Vec3 end = beam.origin.add(beam.direction.scale(beam.range));
        BlockHitResult hit = mc.level.clip(new ClipContext(beam.origin, end, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, player));
        if (hit.getType() == HitResult.Type.BLOCK) {
            float wall = (float) hit.getLocation().distanceTo(beam.origin);
            float range = Math.min(beam.range, wall / FADE_START + 1.0F);
            ((FlashlightBeamRangeAccess) (Object) beam).rpfixes$setRange(range);
        }
    }

    private static boolean enabled() {
        try {
            return FixesConfig.SA_FLASHLIGHT_OCCLUSION.get();
        } catch (IllegalStateException notLoadedYet) {
            return true;
        }
    }
}
