package faygolover.rpfixes.mixin.customnpcs;

import faygolover.rpfixes.fix.CnpcPlayerSkins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

/** Custom NPCs не затирает настоящий скин игрока стандартным — см. {@link CnpcPlayerSkins}. */
@Pseudo
@Mixin(targets = "noppes.npcs.client.SkinUtil", remap = false)
public abstract class SkinUtilMixin {
    @Inject(method = "resetSkin(Ljava/util/UUID;)V", at = @At("HEAD"), cancellable = true)
    private static void rpfixes$keepOwnSkin(UUID uuid, CallbackInfo ci) {
        if (CnpcPlayerSkins.keepOwnSkin(uuid)) ci.cancel();
    }
}
