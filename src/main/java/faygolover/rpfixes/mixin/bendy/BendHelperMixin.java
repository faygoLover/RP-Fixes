package faygolover.rpfixes.mixin.bendy;

import dev.kosmx.playerAnim.impl.animation.BendHelper;
import dev.kosmx.playerAnim.impl.animation.IBendHelper;
import faygolover.rpfixes.FixesConfig;
import faygolover.rpfixes.fix.LazyBends;
import io.github.kosmx.bendylib.ModelPartAccessor;
import io.github.kosmx.bendylib.impl.BendableCuboid;
import io.github.kosmx.bendylib.impl.ICuboid;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Сгибы player-animation-lib через bendy-lib — только там, где они возможны. BendHelper.bend берёт у куска модели
 * сгибатель «bend» и сразу гнёт его; у кусков, которые собрал другой мод (EMF/Fresh Animations, оружейные модели),
 * сгибателя нет — getAndActivateMutator возвращает null и игра падает (прогон A 16.10: удар Better Combat). Здесь то же
 * самое, но кусок без сгибателя сначала получает его — так же, как его вешает player-animation-lib на обычную модель
 * (тело и куртка гнутся вниз, руки, ноги, рукава и штанины — вверх). Так гнутся и руки/ноги моделей EMF (FA+Player),
 * а голову эмоции не гнут — глаза и мимика FA остаются. Не получилось — кусок просто не гнётся.
 */
@Mixin(value = BendHelper.class, remap = false)
public abstract class BendHelperMixin {

    @Inject(method = "bend(Lnet/minecraft/client/model/geom/ModelPart;FF)V", at = @At("HEAD"), cancellable = true)
    private void rpfixes$safeBend(ModelPart modelPart, float axis, float rotation, CallbackInfo ci) {
        if (!FixesConfig.SAFE_BENDS.get()) return;
        ModelPartAccessor.optionalGetCuboid(modelPart, 0).ifPresent(cuboid -> {
            ICuboid mutator = cuboid.getAndActivateMutator("bend");
            if (mutator == null && FixesConfig.LAZY_BENDS.get()) {
                String part = LazyBends.PART.get();
                IBendHelper.INSTANCE.initBend(modelPart, "torso".equals(part) || "body".equals(part) ? Direction.DOWN : Direction.UP);
                mutator = cuboid.getAndActivateMutator("bend");
            }
            if (mutator instanceof BendableCuboid bendable) bendable.applyBend(axis, rotation);
        });
        ci.cancel();
    }
}
