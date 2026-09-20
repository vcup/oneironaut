package net.beholderface.oneironaut.block.blockentity;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.items.pigment.ItemDyePigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import kotlin.collections.CollectionsKt;
import net.beholderface.oneironaut.block.WispBattery;
import net.beholderface.oneironaut.block.WispBatteryFake;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import ram.talia.hexal.api.FunUtilsKt;
import ram.talia.hexal.common.entities.WanderingWisp;

import java.util.ArrayList;

import static net.beholderface.oneironaut.block.blockentity.WispBatteryEntity.getColors;

public class WispBatteryEntityFake extends BlockEntity {
    public WispBatteryEntityFake(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.WISP_BATTERY_ENTITY_DECORATIVE.get(), pos, state);
    }

    public static int[] getColors(Random random){
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < 32; i++){
            for (ItemDyePigment color : HexItems.DYE_PIGMENTS.values()){
                colors.add(FunUtilsKt.nextColour((new FrozenPigment(new ItemStack(color), Util.NIL_UUID)), random));
            }
        }
        return CollectionsKt.toIntArray(colors);
    }

    public void tick(World world, BlockPos pos, BlockState state){
        //only do anything when powered
        if (state.get(WispBatteryFake.REDSTONE_POWERED)){
            if (world.isClient){
                Vec3d doublePos = new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                int[] colors = getColors(world.random);
                world.addParticle(
                        new ConjureParticleOptions(colors[world.random.nextInt(colors.length)]),
                        doublePos.x, doublePos.y, doublePos.z,
                        0.0125 * (world.random.nextDouble() - 0.5),
                        0.0125 * (world.random.nextDouble() - 0.5),
                        0.0125 * (world.random.nextDouble() - 0.5)
                );
            } else {
                if (world.getTime() % 80 == 0 && world.getEntitiesByClass(WanderingWisp.class, Box.of(Vec3d.ofCenter(pos), 64.0, 64.0, 64.0), (idfk)-> true).size() < 20){
                    WanderingWisp wisp = new WanderingWisp(world, Vec3d.ofCenter(pos, 1));
                    wisp.setPigment(new FrozenPigment(
                            new ItemStack(CollectionsKt.elementAt(HexItems.DYE_PIGMENTS.values(),
                                    world.random.nextInt(HexItems.DYE_PIGMENTS.size()))),
                            Util.NIL_UUID
                    ));
                    OneironautPlatform.setDecorativeWisp(wisp, true);
                    world.spawnEntity(wisp);
                }
            }
        }
    }
}
