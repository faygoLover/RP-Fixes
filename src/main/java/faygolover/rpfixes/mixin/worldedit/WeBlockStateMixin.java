package faygolover.rpfixes.mixin.worldedit;

import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import faygolover.rpfixes.fix.WorldEditLazyStates;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

/** Состояния одного типа блока построены (лениво, при первом обращении) — регистрируем их ID, как раньше делал ready(). */
@Mixin(value = BlockState.class, remap = false)
public abstract class WeBlockStateMixin {
    @Inject(method = "generateStateMap", at = @At("RETURN"))
    private static void rpfixes$registerType(BlockType blockType, CallbackInfoReturnable<Map<Map<Property<?>, Object>, BlockState>> cir) {
        WorldEditLazyStates.onGenerated(cir.getReturnValue().values());
    }
}
