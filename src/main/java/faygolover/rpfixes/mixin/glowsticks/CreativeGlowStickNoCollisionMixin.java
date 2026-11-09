package faygolover.rpfixes.mixin.glowsticks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;

/** Glow Sticks 7.5.1: творческая ХИС без коллизии, как и обычная ({@link GlowStickNoCollisionMixin}). */
@Mixin(targets = "de.markusbordihn.glowsticks.block.CreativeGlowStickBlock")
public abstract class CreativeGlowStickNoCollisionMixin extends Block {
    private CreativeGlowStickNoCollisionMixin(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }
}
