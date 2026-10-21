package faygolover.rpfixes.mixin.debuglimits;

import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Включены ли границы чанков (поле закрыто). */
@Mixin(DebugRenderer.class)
public interface DebugRendererAccess {
    @Accessor("renderChunkborder")
    boolean rpfixes$chunkBorder();
}
