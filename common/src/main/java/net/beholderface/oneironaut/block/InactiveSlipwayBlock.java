package net.beholderface.oneironaut.block;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class InactiveSlipwayBlock extends Block {
    public InactiveSlipwayBlock(Settings settings) {
        super(settings);
    }
    private static List<Integer> colors;
    public static void init(){
        colors();
    }

    /**
     * Initialisation on demand. Architectury's CLIENT_STARTED event does not reach a listener that the
     * mod registers from inside its own client init on NeoForge, so relying on {@link #init()} left the
     * colour cache null there and the inactive slipway silently emitted no particles.
     */
    private static List<Integer> colors(){
        List<Integer> current = colors;
        if (current == null){
            Random random = Random.create();
            List<Integer> colorList = new ArrayList<>();
            for (int i = 0; i < 32; i++){
                for(ItemDyePigment pigment : HexItems.DYE_PIGMENTS.values()){
                    FrozenPigment frozen = new FrozenPigment(new ItemStack(pigment), Util.NIL_UUID);
                    colorList.add(ram.talia.hexal.api.FunUtilsKt.nextColour(frozen, random));
                }
            }
            current = colorList;
            colors = current;
        }
        return current;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){
        return VoxelShapes.empty();
    }

    @Override
    public BlockRenderType getRenderType(BlockState state){
        return BlockRenderType.MODEL;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        List<Integer> colors = colors();
        if (colors != null){
            Vec3d particleCenter = Vec3d.ofCenter(pos);
            for(ItemDyePigment pigment : HexItems.DYE_PIGMENTS.values()){
                int color = colors.get(random.nextInt(colors.size()));
                Vec3d particlePoint = new Vec3d(
                        (particleCenter.x + 0.35 * random.nextGaussian()),
                        (particleCenter.y + 0.35 * random.nextGaussian()),
                        (particleCenter.z + 0.35 * random.nextGaussian()));
                world.addParticle(new ConjureParticleOptions(color),
                        particlePoint.x,
                        particlePoint.y,
                        particlePoint.z,
                        0.0125 * (random.nextDouble() - 0.5),
                        0.0125 * (random.nextDouble() - 0.5),
                        0.0125 * (random.nextDouble() - 0.5));
            }
        }
    }
}
