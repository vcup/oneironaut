package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.block.blockentity.HoverElevatorBlockEntity;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SuperBuddingBlock extends Block /*extends BuddingAmethystBlock*/ {
    public SuperBuddingBlock(Settings settings){
        super(settings
                .ticksRandomly()
                .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                .hardness(3.5f));
    }

    public static final int GROW_CHANCE = 3;
    private static final Direction[] DIRECTIONS = Direction.values();

    //shamelessly stolen from vanilla code
    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(GROW_CHANCE) == 0) {
            Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (Oneironaut.isWorldNoosphere(world)){
                if (canGrowIn(blockState)) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_BUD_SMALL.get();
                } else if (blockState.isOf(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_SMALL.get()) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_BUD_MEDIUM.get();
                } else if (blockState.isOf(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_MEDIUM.get()) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_BUD_LARGE.get();
                } else if (blockState.isOf(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_LARGE.get()) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_CLUSTER.get();
                }

                if (block != null) {
                    BlockState blockState2 = (BlockState)((BlockState)block.getDefaultState().with(AmethystClusterBlock.FACING, direction)).with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                    world.setBlockState(blockPos, blockState2);
                }

            } else {
                if (canGrowIn(blockState)) {
                    block = Blocks.MEDIUM_AMETHYST_BUD;
                } else if (blockState.isOf(Blocks.MEDIUM_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                    block = Blocks.LARGE_AMETHYST_BUD;
                } else if (blockState.isOf(Blocks.LARGE_AMETHYST_BUD) && blockState.get(AmethystClusterBlock.FACING) == direction) {
                    block = Blocks.AMETHYST_CLUSTER;
                }

                if (block != null) {
                    BlockState blockState2 = (BlockState)((BlockState)block.getDefaultState().with(AmethystClusterBlock.FACING, direction)).with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
                    world.setBlockState(blockPos, blockState2);
                }
            }
        }
    }

    public static boolean canGrowIn(BlockState state) {
        return state.isAir() || state.isOf(Blocks.WATER) && state.getFluidState().getLevel() == 8;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Random rand = world.random;
        Vec3d particleCenter = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
        int limit = rand.nextBetween(3, 6);
        for (int i = 0; i < limit; i++){
            world.addParticle(new ConjureParticleOptions(HoverElevatorBlockEntity.color()),
                    particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 7), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 7),
                    particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 7), 0.0, 0.0, 0.0);
        }
    }
}
