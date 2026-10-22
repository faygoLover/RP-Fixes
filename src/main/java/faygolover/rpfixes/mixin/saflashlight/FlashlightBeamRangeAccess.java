package faygolover.rpfixes.mixin.saflashlight;

import com.ogaba.sa_survival.client.flashlight.FlashlightBeam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Дальность луча фонаря Survivor's Arsenal ({@code final} у мода) — для {@link FlashlightBeamOcclusionMixin}. */
@Mixin(value = FlashlightBeam.class, remap = false)
public interface FlashlightBeamRangeAccess {
    @Mutable
    @Accessor("range")
    void rpfixes$setRange(float range);
}
