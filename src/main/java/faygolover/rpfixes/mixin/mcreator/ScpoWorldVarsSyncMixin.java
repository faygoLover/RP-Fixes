package faygolover.rpfixes.mixin.mcreator;

import faygolover.rpfixes.fix.McreatorSync;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** SCP: Overtime, переменные измерения: рассылка syncData только при изменении (см. McreatorSync). */
@Mixin(targets = "net.scpo.network.ScpoModVariables$WorldVariables", remap = false)
public abstract class ScpoWorldVarsSyncMixin {
    @Inject(method = "syncData", at = @At("HEAD"), cancellable = true)
    private void rpfixes$onlyChanged(LevelAccessor world, CallbackInfo ci) {
        if (world instanceof Level l && !l.isClientSide() && !McreatorSync.savedChanged((SavedData) (Object) this)) ci.cancel();
    }
}
