package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

/**
 * Thought slurry.
 * <p>
 * Only the {@link Still} and {@link Flowing} subclasses are ever instantiated; both instances are
 * registered under {@link #ID} and {@link #FLOWING_ID} and are also available as the
 * {@link #STILL_FLUID} / {@link #FLOWING_FLUID} constants.
 * <p>
 * Those constants matter beyond convenience. Vanilla's {@code FluidBlock} caches one {@code FluidState}
 * per {@code level} value while the block is being constructed, so {@link #getStill()} must not go
 * through the Fluid registry: on Forge/NeoForge the registry is still empty at that point, which made
 * every cached state the <em>empty</em> fluid and left the fluid block with nothing to flow or draw.
 * See {@link ThoughtSlurryBlock#getFluidState(BlockState)} for the other half of that trap.
 */
public abstract class ThoughtSlurry extends FlowableFluid {
    public static final Identifier ID = Oneironaut.id("thought_slurry");
    public static final Identifier FLOWING_ID = Oneironaut.id("flowing_thought_slurry");

    public static final TagKey<Fluid> TAG = TagKey.of(RegistryKeys.FLUID, ID);

    public static final ThoughtSlurry.Flowing FLOWING_FLUID = new ThoughtSlurry.Flowing();
    public static final ThoughtSlurry.Still STILL_FLUID = new ThoughtSlurry.Still();

    @Override
    public boolean matchesType(Fluid fluid) {
        return fluid == getStill() || fluid == getFlowing();
    }

    /**
     * The flowing singleton, deliberately not a registry lookup -- see the class comment.
     */
    @Override
    public Fluid getFlowing() {
        return FLOWING_FLUID;
    }

    /**
     * The still singleton, deliberately not a registry lookup -- see the class comment.
     */
    @Override
    public Fluid getStill() {
        return STILL_FLUID;
    }

    @Override
    public FluidState getFlowing(int level, boolean falling) {
        return (this.getFlowing().getDefaultState().with(LEVEL, level)).with(FALLING, falling);
    }

    @Override
    public abstract boolean isStill(FluidState state);

    @Override
    public abstract int getLevel(FluidState state);

    @Override
    protected boolean isInfinite(World world) {
        return true;
    }

    @Override
    protected void beforeBreakingBlock(WorldAccess world, BlockPos pos, BlockState state) {
        final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropStacks(state, world, pos, blockEntity);
    }

    @Override
    protected int getFlowSpeed(WorldView world) {
        return 3;
    }

    @Override
    protected int getLevelDecreasePerBlock(WorldView world) {
        return 1;
    }

    @Override
    public Item getBucketItem() {
        return OneironautItemRegistry.THOUGHT_SLURRY_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickRate(WorldView world) {
        return 5;
    }

    @Override
    protected float getBlastResistance() {
        return 100.0f;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ThoughtSlurryBlock.INSTANCE.getDefaultState().with(FluidBlock.LEVEL, getBlockStateLevel(state));
    }

    public static class Flowing extends ThoughtSlurry {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(FlowableFluid.LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return false;
        }

        @Override
        public int getLevel(FluidState state) {
            return state.get(FlowableFluid.LEVEL);
        }
    }

    public static class Still extends ThoughtSlurry {
        @Override
        protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
            super.appendProperties(builder);
            builder.add(FlowableFluid.LEVEL);
        }

        @Override
        public boolean isStill(FluidState state) {
            return true;
        }

        @Override
        public int getLevel(FluidState state) {
            return 8;
        }
    }
}
