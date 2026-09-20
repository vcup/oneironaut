package net.beholderface.oneironaut.block.blockentity;

import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.misc.PlayerPositionRecorder;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.datafixers.util.Pair;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.block.HoverElevatorBlock;
import net.beholderface.oneironaut.network.HoverliftAntiDesyncPacket;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.beholderface.oneironaut.registry.OneironautBlockRegistry;
import net.beholderface.oneironaut.registry.OneironautTags;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.*;

import static net.beholderface.oneironaut.MiscAPIKt.vecProximity;

public class HoverElevatorBlockEntity extends BlockEntity {

    public static final Map<LivingEntity, Integer> SERVER_HOVER_MAP = new HashMap<>();
    public static final Map<LivingEntity, Integer> CLIENT_HOVER_MAP = new HashMap<>();
    private static Pair<Long, Boolean> LAST_CALL;// = new Pair<>(0L, null);
    private static final DirectionProperty FACING = HoverElevatorBlock.FACING;
    /**
     * Initialisation-on-demand holder: Hex Casting's item registry is not populated yet when this
     * class is first initialised on Forge/NeoForge, so the colour cannot be captured in a static field
     * of the outer class. The holder gives thread-safe, race-free lazy resolution.
     */
    private static final class DefaultColor {
        static final int VALUE = new FrozenPigment(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE).getDefaultStack(), Util.NIL_UUID)
                .getColorProvider().getColor(0f, Vec3d.ZERO);
    }

    public static int color() {
        return DefaultColor.VALUE;
    }

    private Box pairCuboid = null;
    private int level = 0;
    private final Box defaultCuboid;

    public HoverElevatorBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.HOVER_ELEVATOR_ENTITY.get(), pos, state);
        this.defaultCuboid = new Box(pos);
    }

    public void tick(World world, BlockPos pos, BlockState state){
        Random rand = world.random;
        Direction dir = state.get(FACING);
        Vec3i dirVec = dir.getVector();
        Vec3d dirVec3d = new Vec3d(dirVec.getX(), dirVec.getY(), dirVec.getZ());
        int num = 0;
        if (state.get(HoverElevatorBlock.POWERED)){
            if (this.pairCuboid == null || world.getTime() % 20 == 0){
                //search for paired elevator
                this.pairCuboid = findPair(world, pos, state);
                /*if (!this.pairCuboid.equals(this.defaultCuboid)){
                    Oneironaut.LOGGER.info("Found paired elevator");
                }*/
            }
            List<LivingEntity> detectedEntities = new ArrayList<>();
            for (Entity e : world.getOtherEntities(null, pairCuboid, (entity)-> {return true;})){
                if (e instanceof LivingEntity le){
                    if (le instanceof PlayerEntity player && (player.isSpectator() || player.getAbilities().flying)){
                        continue;
                    }
                    detectedEntities.add(le);
                    /*if (world.getTime() % 20 == 0 && !world.isClient){
                        Oneironaut.LOGGER.info("Found living entity " + le.getDisplayName());
                    }*/
                }
            }
            num = Math.min(detectedEntities.size(), 15);
            //world.updateListeners(pos, state, state.with(HoverElevatorBlock.LEVEL, Math.max(detectedEntities.size(), 15)), 2);
            int axialBit = switch (state.get(FACING).getAxis()){
                case X -> 1;
                case Y -> 2;
                case Z -> 4;
            };
            Map<LivingEntity, Integer> relevantMap = !world.isClient ? SERVER_HOVER_MAP : CLIENT_HOVER_MAP;
            for (LivingEntity livingEntity : detectedEntities){
                int found = relevantMap.getOrDefault(livingEntity, 0);
                relevantMap.put(livingEntity, found | axialBit);
                Vec3d entityPos = livingEntity.getPos().add(livingEntity.getBoundingBox().getXLength() / 2, 0, livingEntity.getBoundingBox().getZLength() / 2);
                Vec3d entityVel = livingEntity.getVelocity();
                if (world.isClient && world instanceof ClientWorld clientWorld){
                    clientWorld.addParticle(new ConjureParticleOptions(livingEntity instanceof PlayerEntity player ?
                                    IXplatAbstractions.INSTANCE.getPigment(player).getColorProvider().getColor(world.getTime(), player.getPos()) : color()),
                            entityPos.x + (((rand.nextGaussian() * 2) - 1) / 5), entityPos.y + (((rand.nextGaussian() * 2) - 1) / 5) + (rand.nextBetween(0, (int) (livingEntity.getHeight() * 20f)) / 20f),
                            entityPos.z + (((rand.nextGaussian() * 2) - 1) / 5), entityVel.x, entityVel.y + 0.1, entityVel.z);
                }
            }
            if (world.isClient && world instanceof ClientWorld clientWorld && !pairCuboid.equals(defaultCuboid)){
                Vec3d particleCenter = Vec3d.ofCenter(new Vec3i(pos.getX(), pos.getY(), pos.getZ())).add(dirVec3d.multiply(0.5));
                Vec3d dirVelVec = dirVec3d.multiply(0.25);
                if (rand.nextBetween(1, 10) <= 3){
                    clientWorld.addParticle(new ConjureParticleOptions(color()),
                            particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 7), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 7),
                            particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 7), dirVelVec.x, dirVelVec.y, dirVelVec.z);
                }
            }
        }
        if (world.getTime() % 10 == 0){
            this.updateLevel(num, world, pos);
        }
    }

    private void updateLevel(int newLevel, World world, BlockPos pos){
        if (this.level != newLevel){
            this.level = newLevel;
            world.updateNeighbors(pos, OneironautBlockRegistry.HOVER_ELEVATOR.get());
        }
    }

    public int getLevel(){
        return this.level;
    }

    private Box findPair(World world, BlockPos pos, BlockState state){
        Direction dir = state.get(FACING);
        Vec3i dirVec = dir.getVector();
        BlockPos current;
        Box output = this.defaultCuboid;
        int i = 1;
        int initialRange = (dir.getAxis() == Direction.Axis.Y ? 128 : 64) + 1;
        int adjustedRange = initialRange;
        int repeaters = 0;
        for (; i <= adjustedRange; i++){
            current = pos.add(dirVec.multiply(i));
            BlockState checkedState = world.getBlockState(current);
            if (checkedState.getBlock() == OneironautBlockRegistry.HOVER_ELEVATOR.get()){
                if (checkedState.get(HoverElevatorBlock.POWERED)){
                    if(checkedState.get(FACING).getOpposite().equals(dir)){
                        output = new Box(pos, current.add(1, 1, 1));
                        break;
                    } else if (checkedState.get(FACING).equals(dir)) {
                        //not sure if there would be any use case for >   << but it feels weird to let you do that
                        break;
                    }
                }
            } else if (checkedState.getBlock() == OneironautBlockRegistry.HOVER_REPEATER.get() && repeaters < 3){
                adjustedRange = i + (initialRange - 1);
                repeaters++;
            } else if (checkedState.isIn(OneironautTags.Blocks.breakImmune) || checkedState.isIn(OneironautTags.Blocks.blocksRaycast)){
                break;
            }
        }
        return output;
    }

    private static Set<ServerPlayerEntity> RECENT_USERS = new HashSet<>();
    public static void processHover(boolean isServer, long timestamp){
        Map<LivingEntity, Integer> relevantMap = isServer ? SERVER_HOVER_MAP : CLIENT_HOVER_MAP;
        double threshold = 0.75;
        if (LAST_CALL != null){
            if (!isServer && MinecraftClient.getInstance().world == null){
                //Oneironaut.LOGGER.info("No client world present, deleting last hoverlift call information.");
                LAST_CALL = null;
                relevantMap.clear();
                return;
            }/* else if (isServer == LAST_CALL.getSecond()){
                Oneironaut.LOGGER.info("Client/server issue processing hoverlift. Skipping.   " + isServer + " " + LAST_CALL.getSecond());
                return;
            }*/ else if (LAST_CALL.getFirst() >= timestamp){
                if (timestamp != -1){
                    //Oneironaut.LOGGER.info("Wrong hoverlift processing timestamp. Skipping.");
                    relevantMap.clear();
                    return;
                } else {
                    LAST_CALL = new Pair<>(timestamp, isServer);
                }
            }
        } else if (!isServer && MinecraftClient.getInstance().world == null) {
            relevantMap.clear();
            return;
        } else {
            LAST_CALL = new Pair<>(timestamp, isServer);
        }
        for (LivingEntity entity : relevantMap.keySet()){
            Vec3d hoverVec = Vec3d.ZERO;
            Vec3d look = entity.getRotationVector();
            int axesNum = relevantMap.getOrDefault(entity, 0);
            int divisor = 15;
            boolean counterVerticalMomentum = true;
            boolean up = (axesNum & 2) == 2;
            if (!entity.isSneaking()){
                if ((axesNum & 1) == 1){
                    double eastScore = vecProximity(Direction.EAST, look);
                    double westScore = vecProximity(Direction.WEST, look);
                    if ((eastScore <= threshold || westScore <= threshold) && Math.abs(hoverVec.x) < Math.abs(look.x / divisor)){
                        hoverVec = hoverVec.add(look.x * (1.0 / divisor) * (1 - Math.min(eastScore, westScore)), 0.0, 0.0);
                    }
                }
                if (up){
                    double upScore = vecProximity(Direction.UP, look);
                    double downScore = vecProximity(Direction.DOWN, look);
                    if ((upScore <= threshold || downScore <= threshold) && Math.abs(hoverVec.y) < Math.abs(look.y / divisor)){
                        hoverVec = hoverVec.add(0.0, look.y * (1.0 / divisor) * (1 - Math.min(upScore, downScore)), 0.0);
                        counterVerticalMomentum = false;
                    }
                }
                if ((axesNum & 4) == 4){
                    double southScore = vecProximity(Direction.SOUTH, look);
                    double northScore = vecProximity(Direction.NORTH, look);
                    if ((southScore <= threshold || northScore <= threshold) && Math.abs(hoverVec.z) < Math.abs(look.z / divisor)){
                        hoverVec = hoverVec.add(0.0, 0.0, look.z * (1.0 / divisor) * (1 - Math.min(southScore, northScore)));
                    }
                }
            }
            boolean lookingUp = vecProximity(Direction.UP, look) <= threshold;
            if ((entity.getWorld().getTime() % 10 == 0 || !entity.hasStatusEffect(StatusEffects.SLOW_FALLING)) && !(lookingUp && up)){
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, entity.isSneaking() ? 60 : 11, 0, true, false, true));
            }
            Vec3d velocity = entity.getVelocity();
            if (entity instanceof ServerPlayerEntity serverPlayerEntity){
                velocity = PlayerPositionRecorder.getMotion(serverPlayerEntity);
                if (serverPlayerEntity.hasStatusEffect(StatusEffects.SLOW_FALLING)){
                    RECENT_USERS.add(serverPlayerEntity);
                }
            }
            double antigravNum = lookingUp && up ? 0.08 : 0.01;
            boolean applyAntigrav = true;
            if (counterVerticalMomentum){
                if (velocity.y < -0.0125){
                    hoverVec = hoverVec.add(0.0, 0.025, 0.0);
                } else if (velocity.y > 0.01){
                    applyAntigrav = false;
                    //this feels kinda jank but it's the only thing that's reliably worked for stopping upwards motion
                    //I do not know why
                    hoverVec = hoverVec.add(0.0, velocity.y * -1, 0.0);
                }
            }
            hoverVec = hoverVec.add(new Vec3d(0.0, applyAntigrav ? antigravNum : 0.0, 0.0));
            entity.addVelocity(hoverVec.x, hoverVec.y, hoverVec.z);
        }
        if (isServer){
            MinecraftServer server = Oneironaut.getCachedServer();
            if (server.getOverworld().getTime() % 10 == 0){
                for (ServerPlayerEntity player : RECENT_USERS){
                    if (!player.hasStatusEffect(StatusEffects.SLOW_FALLING)){
                        HoverliftAntiDesyncPacket packet = new HoverliftAntiDesyncPacket();
                        OneironautPlatform.sendToPlayer(player, packet);
                    }
                }
                RECENT_USERS.clear();
            }
        }
        relevantMap.clear();
    }
}
