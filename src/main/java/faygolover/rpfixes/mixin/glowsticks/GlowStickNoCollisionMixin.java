package faygolover.rpfixes.mixin.glowsticks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Glow Sticks 7.5.1: лежащая ХИС без коллизии. Падающий приземлялся на её плоскую рамку, а не на мягкий блок под ней, и
 * получал полный урон. Рамка выделения остаётся.
 */
@Mixin(targets = "de.markusbordihn.glowsticks.block.GlowStickBlock")
public abstract class GlowStickNoCollisionMixin extends FallingBlock {
    private GlowStickNoCollisionMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
