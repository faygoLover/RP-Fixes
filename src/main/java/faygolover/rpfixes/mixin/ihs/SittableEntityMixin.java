package faygolover.rpfixes.mixin.ihs;

import faygolover.rpfixes.FixesConfig;
import net.boat.industrialhellscape.block.modded_block_entities.SittableEntity.SittableEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * industrialhellscape 0.3.6A, сиденье стульев ({@code SittableEntity}, офисные кресла): сущность ставится с поворотом 0
 * и поворот седока никак не держит. У других тело сидящего оставалось, как было при посадке, и поворачивалось только
 * при сильном повороте головы — сидит за столом спиной к нему. Здесь каждый тик (на каждом клиенте и на сервере)
 * тело седока смотрит туда же, куда стул ({@code facing} блока под сиденьем: спинка у модели с обратной стороны),
 * голова — не дальше 85° от тела (как у ванилы на лошади). Взгляд (камера) не ограничен.
 */
@Mixin(SittableEntity.class)
public abstract class SittableEntityMixin extends Entity {
    @Unique private static final float RPFIXES_MAX_HEAD = 85.0F;

    protected SittableEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void positionRider(Entity passenger, Entity.MoveFunction move) {
        super.positionRider(passenger, move);
        if (!(passenger instanceof LivingEntity rider) || !hasPassenger(passenger)) return;
        boolean on;
        try { on = FixesConfig.IHS_CHAIR_FACING.get(); } catch (IllegalStateException notLoaded) { on = false; }
        if (!on) return;
        Direction facing = level().getBlockState(blockPosition()).getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(null);
        if (facing == null) return;
        float body = facing.toYRot();
        rider.yBodyRot = body;
        rider.yBodyRotO = body;
        rider.yHeadRot = body + Mth.clamp(Mth.wrapDegrees(rider.yHeadRot - body), -RPFIXES_MAX_HEAD, RPFIXES_MAX_HEAD);
        rider.yHeadRotO = body + Mth.clamp(Mth.wrapDegrees(rider.yHeadRotO - body), -RPFIXES_MAX_HEAD, RPFIXES_MAX_HEAD);
    }
}
