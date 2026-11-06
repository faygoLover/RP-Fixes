package faygolover.rpfixes.mixin.ihs;

import faygolover.rpfixes.FixesConfig;
import net.boat.industrialhellscape.block.modded_block_entities.SittableEntity.SittableEntity;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * industrialhellscape 0.3.6A, сиденье стульев ({@code SittableEntity}, офисные кресла): сущность ставится с поворотом 0
 * и поворот седока никак не держит. Сделано как стулья SovietEraV3 ({@code SeatEntity}), но у кресел IHS вперёд —
 * сам {@code facing} блока: в модели при {@code facing=north} спинка на южной стороне (z 11–14). При посадке сервер поворачивает игрока вперёд; дальше взгляд не уходит
 * дальше 80° от стула — ограничение у клиента седока при каждом движении мыши (как у лодки), без поворотов каждый тик.
 */
@Mixin(SittableEntity.class)
public abstract class SittableEntityMixin extends Entity {
    @Unique private static final float RPFIXES_MAX_TURN = 80.0F;

    protected SittableEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Unique
    private static boolean rpfixes$on() {
        try { return FixesConfig.IHS_CHAIR_FACING.get(); } catch (IllegalStateException notLoaded) { return false; }
    }

    /** Куда смотрит сидящий: от спинки вперёд. {@code null} — под сиденьем не стул с направлением. */
    @Unique
    private Float rpfixes$seatYaw() {
        Direction facing = level().getBlockState(blockPosition()).getOptionalValue(BlockStateProperties.HORIZONTAL_FACING).orElse(null);
        return facing == null ? null : facing.toYRot();
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (level().isClientSide || !(passenger instanceof ServerPlayer player) || !rpfixes$on()) return;
        Float seat = rpfixes$seatYaw();
        if (seat == null) return;
        player.setYBodyRot(seat);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition().add(Vec3.directionFromRotation(0.0F, seat)));
    }

    @Override
    public void onPassengerTurned(Entity passenger) {
        super.onPassengerTurned(passenger);
        if (!rpfixes$on()) return;
        Float seat = rpfixes$seatYaw();
        if (seat == null) return;
        passenger.setYBodyRot(seat);
        float delta = Mth.wrapDegrees(passenger.getYRot() - seat);
        float clamped = Mth.clamp(delta, -RPFIXES_MAX_TURN, RPFIXES_MAX_TURN);
        passenger.yRotO += clamped - delta;
        passenger.setYRot(passenger.getYRot() + clamped - delta);
        passenger.setYHeadRot(passenger.getYRot());
    }
}
