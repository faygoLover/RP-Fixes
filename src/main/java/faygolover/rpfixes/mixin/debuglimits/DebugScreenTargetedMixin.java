package faygolover.rpfixes.mixin.debuglimits;

import faygolover.rpfixes.fix.PlayerDebugLimits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Правая колонка F3 у игроков без op 2 — только система (Java, память, процессор, экран), без блока, жидкости и
 * сущности под прицелом — см. {@link PlayerDebugLimits}. Левая колонка (координаты и т. п.) не меняется.
 */
@Mixin(DebugScreenOverlay.class)
public abstract class DebugScreenTargetedMixin {
    @Redirect(method = "getSystemInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;showOnlyReducedInfo()Z"))
    private boolean rpfixes$noTargeted(Minecraft mc) {
        return mc.showOnlyReducedInfo() || PlayerDebugLimits.restricted();
    }
}
