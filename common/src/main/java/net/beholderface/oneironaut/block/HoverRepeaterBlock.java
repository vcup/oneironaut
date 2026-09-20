package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.beholderface.oneironaut.block.blockentity.HoverElevatorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class HoverRepeaterBlock extends Block {
    public HoverRepeaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        Random rand = world.random;
        Vec3d particleCenter = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ())).add(0.0, 0.2, 0.0);
        int limit = rand.nextBetween(1, 3);
        for (int i = 0; i < limit; i++){
            world.addParticle(new ConjureParticleOptions(HoverElevatorBlockEntity.color()),
                    particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 50), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 50),
                    particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 50), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        VoxelShape center = VoxelShapes.cuboid(7f / 16, 2f / 16, 7f / 16, 9f / 16, 12f / 16, 9f / 16);
        VoxelShape base1 = VoxelShapes.cuboid(5f / 16, 3f / 16, 5f / 16, 11f / 16, 4f / 16, 11f / 16);
        VoxelShape base2 = VoxelShapes.cuboid(6f / 16, 2f / 16, 6f / 16, 10f / 16, 3f / 16, 10f / 16);
        return VoxelShapes.union(center, base1, base2);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }
}
