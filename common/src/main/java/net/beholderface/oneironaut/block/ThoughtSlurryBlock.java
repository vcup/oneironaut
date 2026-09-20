package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.MapColor;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class ThoughtSlurryBlock extends FluidBlock {
    public static final Identifier ID = Oneironaut.id("thought_slurry");
    public static final AbstractBlock.Settings SETTINGS =
            AbstractBlock.Settings.copy(Blocks.WATER).nonOpaque().mapColor(MapColor.PURPLE);
    public static final ThoughtSlurryBlock INSTANCE =
            new ThoughtSlurryBlock(ThoughtSlurry.STILL_FLUID, SETTINGS);

    public ThoughtSlurryBlock(ThoughtSlurry thoughtSlurry, AbstractBlock.Settings settings) {
        super(thoughtSlurry, settings);
        // Vanilla fills each BlockState's cached fluid state (AbstractBlockState#initShapeCache) from
        // Blocks' static initialiser, which only sees the blocks that exist at bootstrap time. A modded
        // fluid block therefore keeps the constructor default -- the EMPTY fluid -- so the chunk mesher
        // had no fluid to draw and thought slurry was invisible on clients. Do it for our own states.
        for (BlockState state : this.getStateManager().getStates()) {
            state.initShapeCache();
        }
    }

    /**
     * Resolved on demand instead of from {@code FluidBlock}'s cached list.
     * <p>
     * Vanilla's {@code FluidBlock} builds one {@code FluidState} per {@code level} value in its
     * constructor, from {@code fluid.getStill(false)} / {@code getFlowing(...)}. On Forge/NeoForge the
     * block instance is constructed while the shared Fluid registry is still empty, so every entry of
     * that list ended up being the <em>empty</em> fluid: the block was a fluid block with no fluid
     * state, which made thought slurry completely invisible on clients (the fluid renderer had nothing
     * to draw) and broke its flow physics, including the noosphere's ocean of it.
     */
    @Override
    public FluidState getFluidState(BlockState state) {
        int level = Math.min(state.get(LEVEL), 8);
        FlowableFluid slurry = ThoughtSlurry.STILL_FLUID;
        if (level == 0) {
            // A source block. FlowableFluid#getStill only sets FALLING and leaves the fluid LEVEL at the
            // property default (1), and the fluid engine only calls a state a source when LEVEL is 8 --
            // so this has to be set explicitly or the block is treated as a nearly-drained flowing
            // fluid: it spreads away and renders as a sliver.
            return slurry.getStill(false).with(FlowableFluid.LEVEL, 8);
        }
        return level >= 8 ? slurry.getFlowing(8, true) : slurry.getFlowing(8 - level, false);
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }
}
