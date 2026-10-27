package faygolover.rpfixes.mixin.mcreator;

import faygolover.rpfixes.fix.McreatorSync;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.scpo.network.ScpoModVariables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SCP: Overtime: переменные игрока — пакет только при изменении (см. McreatorSync). */
@Mixin(targets = "net.scpo.network.ScpoModVariables$PlayerVariables", remap = false)
public abstract class ScpoPlayerVarsSyncMixin {
    @Inject(method = "syncPlayerVariables", at = @At("HEAD"), cancellable = true)
    private void rpfixes$onlyChanged(Entity entity, CallbackInfo ci) {
        if (entity instanceof ServerPlayer p && !McreatorSync.playerChanged(p, ((ScpoModVariables.PlayerVariables) (Object) this).writeNBT())) ci.cancel();
    }
}
