package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.TransparentBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.beholderface.oneironaut.network.ParticleBurstPacket;
import net.beholderface.oneironaut.platform.OneironautPlatform;

import java.util.UUID;

public class MediaGelBlock extends TransparentBlock {
    public MediaGelBlock(Settings settings) {
        super(settings);
    }
    protected static final VoxelShape COLLISION_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 12.0, 14.0);
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return VoxelShapes.fullCube();
    }

    public VoxelShape getSidesShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.fullCube();
    }

    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return COLLISION_SHAPE;
    }
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 0.2F;
    }

    /**
     * Initialisation-on-demand holder: Hex Casting's item registry is not populated yet when this
     * class is first initialised on Forge/NeoForge, so the pigment cannot be captured in a static
     * field of the outer class. The holder gives thread-safe, race-free lazy resolution.
     */
    private static final class PurpleColorizer {
        static final FrozenPigment VALUE =
                new FrozenPigment(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE).getDefaultStack(), new UUID(0, 0));
    }

    private static FrozenPigment purpleColorizer() {
        return PurpleColorizer.VALUE;
    }
    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        /*double d = Math.abs(entity.getVelocity().y);
        if (d < 0.1 && !entity.bypassesSteppingEffects()) {
            double e = 0.4 + d * 0.2;
            entity.setVelocity(entity.getVelocity().multiply(e, 1.0, e));
        }*/
        if(!entity.bypassesSteppingEffects() && !world.isClient && (world.getTime() % 10) == 0 && entity.isLiving()){
            Vec3d targetPos = entity.getPos().add(0, 0.2, 0);
            OneironautPlatform.sendNear(
                    targetPos,
                    32.0,
                    (ServerWorld) world,
                    new ParticleBurstPacket(targetPos, new Vec3d(0, -0.02, 0), 0.2, 0,
                            purpleColorizer(), 20, false)
                    );
        }
        super.onSteppedOn(world, pos, state, entity);
    }
}
