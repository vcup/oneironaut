package net.beholderface.oneironaut;

import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.common.items.ItemStaff;
import at.petrak.hexcasting.common.lib.HexItems;
import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.events.common.InteractionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.event.events.common.TickEvent;
import net.beholderface.oneironaut.block.InactiveSlipwayBlock;
import net.beholderface.oneironaut.block.blockentity.HoverElevatorBlockEntity;
import net.beholderface.oneironaut.casting.DepartureEntry;
import net.beholderface.oneironaut.casting.DisintegrationProtectionManager;
import net.beholderface.oneironaut.casting.idea.IdeaInscriptionManager;
import net.beholderface.oneironaut.casting.conceptmodification.ConceptModifier;
import net.beholderface.oneironaut.casting.conceptmodification.ConceptModifierManager;
import net.beholderface.oneironaut.item.BottomlessMediaItem;
import net.beholderface.oneironaut.recipe.OneironautRecipeSerializer;
import net.beholderface.oneironaut.recipe.OneironautRecipeTypes;
import net.beholderface.oneironaut.registry.*;
import net.beholderface.oneironaut.status.MediaDisintegrationEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ram.talia.hexal.common.entities.WanderingWisp;

import java.util.*;
import java.util.function.Consumer;

import static net.beholderface.oneironaut.MiscAPIKt.getItemTagKey;
import static net.beholderface.oneironaut.MiscAPIKt.stringToWorld;

/**
 * This is effectively the loading entrypoint for most of your code, at least
 * if you are using Architectury as intended.
 */
public class Oneironaut {
    public static final String MOD_ID = "oneironaut";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    private static final List<Item> randomWispPigments = new ArrayList<>();
    private static ServerWorld noosphere = null;
    private static ServerWorld deepNoosphere = null;
    private static MinecraftServer server = null;


    public static final Set<Pair<LivingEntity, StatusEffectInstance>> reapplicationSet = new HashSet<>();
    private static boolean earlyInitDone = false;
    private static boolean lateInitDone = false;

    /**
     * Everything that has to be known before the loader freezes its registries.
     * <p>
     * Fabric runs this from the mod initializer; NeoForge runs it from the mod constructor, because
     * Forge freezes the vanilla registries immediately after mod construction.
     */
    public static void initEarly() {
        if (earlyInitDone) {
            return;
        }
        earlyInitDone = true;
        LOGGER.info("why do they call it oven when you of in the cold food of out hot eat the food");
        OneironautMiscRegistry.init();
        OneironautBlockRegistry.init();
        OneironautItemRegistry.init();
        OneironautFeatureRegistry.init();
        OneironautRecipeSerializer.init();
        OneironautRecipeTypes.init();
    }

    /**
     * Registers into Hex Casting's own registries and wires up the event listeners.
     * <p>
     * Hex Casting creates its registries from Forge's {@code NewRegistryEvent}, which fires after mod
     * construction, so on NeoForge this must run from {@code FMLCommonSetupEvent} instead of the mod
     * constructor: doing it in the constructor throws
     * {@code IllegalStateException: Registry is already frozen (trying to add key
     * ResourceKey[minecraft:root / hexcasting:iota_type])}. Fabric accepts both calls back to back.
     */
    public static void initLate() {
        if (lateInitDone) {
            return;
        }
        lateInitDone = true;
        OneironautIotaTypeRegistry.init();
        OneironautPatternRegistry.init();

        LifecycleEvent.SERVER_STARTED.register((startedserver) ->{
            server = startedserver;
            noosphere = stringToWorld("oneironaut:noosphere", startedserver);
            deepNoosphere = stringToWorld("oneironaut:deep_noosphere", startedserver);

            if (OneironautMiscRegistry.DISINTEGRATION.get().getAttributeModifiers().isEmpty()){
                OneironautMiscRegistry.DISINTEGRATION.get().addAttributeModifier(EntityAttributes.GENERIC_MAX_HEALTH, MediaDisintegrationEffect.ATTRIBUTE_UUID_STRING, -1.0, EntityAttributeModifier.Operation.ADDITION);
            }

            IdeaInscriptionManager ideaState = IdeaInscriptionManager.getServerState(startedserver);
            IdeaInscriptionManager.cleanMap(startedserver, ideaState);
            ideaState.markDirty();

            DisintegrationProtectionManager disintegrationState = DisintegrationProtectionManager.getServerState(startedserver);
            disintegrationState.cleanEntries();
            disintegrationState.markDirty();

            ConceptModifierManager conceptState = ConceptModifierManager.getServerState(startedserver);
            conceptState.verifyModifiers();
            conceptState.markDirty();

            randomWispPigments.addAll(HexItems.DYE_PIGMENTS.values());
            randomWispPigments.addAll(HexItems.PRIDE_PIGMENTS.values());
            randomWispPigments.add(HexItems.DEFAULT_PIGMENT);
            randomWispPigments.add(HexItems.UUID_PIGMENT);
            randomWispPigments.add(OneironautItemRegistry.PIGMENT_NOOSPHERE.get());
            randomWispPigments.add(OneironautItemRegistry.PIGMENT_FLAME.get());
            randomWispPigments.add(OneironautItemRegistry.PIGMENT_FRENZY.get());
            randomWispPigments.add(OneironautItemRegistry.PIGMENT_CLOCK.get());
            OneironautCastEnvComponents.init();
            InactiveSlipwayBlock.init();
        });

        TickEvent.SERVER_PRE.register((server) -> {
            BottomlessMediaItem.time = server.getOverworld().getTime();
        });

        LOGGER.info("Registering server-side hoverlift processor.");
        TickEvent.SERVER_POST.register((server)->{
            try {
                HoverElevatorBlockEntity.processHover(true, server.getOverworld().getTime());
            } catch (ConcurrentModificationException exception){
                LOGGER.error("Oopsie server-side hoverlift exception {}", exception.getMessage());
            }
            DepartureEntry.clearMap();
            ServerPlayerEntity noospherePlayer = noosphere.getRandomAlivePlayer();
            Random rand = noosphere.random;
            if (noospherePlayer != null && rand.nextInt(1024) == 0){
                double gaussDistance = 16.0;
                WanderingWisp wisp = new WanderingWisp(noosphere, noospherePlayer.getPos().add(
                        rand.nextGaussian() * gaussDistance, rand.nextGaussian() * gaussDistance, rand.nextGaussian() * gaussDistance));
                ItemStack stack = randomWispPigments.get(rand.nextInt(randomWispPigments.size())).getDefaultStack();
                wisp.setPigment(new FrozenPigment(stack, ((Entity)wisp).getUuid()));
                noosphere.spawnEntity(wisp);
            }
            for (Pair<LivingEntity, StatusEffectInstance> pair : reapplicationSet){
                if (!pair.getLeft().hasStatusEffect(pair.getRight().getEffectType())){
                    pair.getLeft().addStatusEffect(pair.getRight());
                }
            }
            reapplicationSet.clear();
        });

        PlayerEvent.PLAYER_RESPAWN.register((player, leavingEnd)->{
            ConceptModifierManager conceptModifierManager = ConceptModifierManager.getServerState(player.server);
            for (ConceptModifier modifier : conceptModifierManager.getAllModifiers(player)){
                modifier.onApply(player);
            }
        });
        PlayerEvent.PLAYER_JOIN.register((player)->{
            ConceptModifierManager conceptModifierManager = ConceptModifierManager.getServerState(player.server);
            for (ConceptModifier modifier : conceptModifierManager.getAllModifiers(player)){
                modifier.onApply(player);
            }
        });

        ItemStack fakeStaffStack = HexItems.STAFF_OAK.getDefaultStack();
        InteractionEvent.RIGHT_CLICK_ITEM.register((player, hand) -> {
            ItemStack heldStack = player.getStackInHand(hand);
            if (heldStack.isIn(OneironautTags.Items.datapackStaves) && !(heldStack.getItem() instanceof ItemStaff)){
                if (heldStack.isIn(HexTags.Items.STAVES)){
                    fakeStaffStack.use(player.getWorld(), player, hand);
                    player.swingHand(hand);
                } else {
                    LOGGER.info(player.getName().getString() + " has right-clicked an item tagged as a datapacked staff, but that item does not have the normal staff tag, which is necessary for the datapack staff functionality to work.");
                }
            }
            return CompoundEventResult.pass();
        });
    }

    public enum Loggers {
        INFO,
        DEBUG,
        WARN,
        ERROR,
        FATAL,
        TRACE
    }

    //for easily toggling whether several things should be logged without having to search through the whole file
    public static void boolLogger(String str, boolean bool){
        boolLogger(str, bool, Loggers.INFO);
    }

    public static void boolLogger(String str, boolean bool, Loggers logType){
        if (bool){
            Consumer<String> logger = switch (logType) {
                case DEBUG -> LOGGER::debug;
                case WARN -> LOGGER::warn;
                case ERROR -> LOGGER::error;
                case FATAL -> LOGGER::fatal;
                case TRACE -> LOGGER::trace;
                default -> LOGGER::info;
            };
            logger.accept(str);
        }
    }

    /**
     * Shortcut for identifiers specific to this mod.
     */
    public static Identifier id(String string) {
        return new Identifier(MOD_ID, string);
    }
    public static ServerWorld getNoosphere(){
        if (noosphere == null){
            throw new IllegalStateException("getNoosphere method called before server start");
        }
        return noosphere;
    }
    public static ServerWorld getDeepNoosphere(){
        if (deepNoosphere == null){
            throw new IllegalStateException("getDeepNoosphere method called before server start");
        }
        return deepNoosphere;
    }
    public static boolean isWorldNoosphere(World world){
        try {
            if (world != null){
                if (world instanceof ServerWorld serverWorld){
                    return serverWorld == noosphere || serverWorld == deepNoosphere;
                } else if (world.isClient){
                    return OneironautClient.isWorldClientNoosphere(world);
                }
            }
        } catch (Exception e){
            //just let it return false
        }
        return false;
    }
    public static boolean isWorldDeepNoosphere(World world){
        try {
            if (world != null){
                if (world instanceof ServerWorld serverWorld){
                    return serverWorld == deepNoosphere;
                } else if (world.isClient){
                    return OneironautClient.isWorldClientDeepNoosphere(world);
                }
            }
        } catch (Exception e){
            //just let it return false
        }
        return false;
    }
    public static MinecraftServer getCachedServer(){
        if (server == null){
            throw new IllegalStateException("getCachedServer method called before server start. or on client. or something else, idfk");
        }
        return server;
    }

    public static boolean isServerThread(){
        if (server != null){
            return Thread.currentThread() == server.getThread();
        }
        return false;
    }

    public static void processDisintegration(LivingEntity entity){
        /*if (entity instanceof ServerPlayerEntity player){
            if (ConceptModifierManager.getServerState(player.server).hasModifierType(player, ConceptModifier.ModifierType.ANTIEROSION)){
                return;
            }
        }*/
        if (!entity.hasStatusEffect(OneironautMiscRegistry.DISINTEGRATION_PROTECTION.get())){
            if (!entity.hasStatusEffect(OneironautMiscRegistry.DISINTEGRATION.get())){
                entity.addStatusEffect(new StatusEffectInstance(OneironautMiscRegistry.DISINTEGRATION.get(), 100, 0, true, true));
            } else {
                StatusEffectInstance instance = entity.getStatusEffect(OneironautMiscRegistry.DISINTEGRATION.get());
                if (instance != null && instance.getDuration() <= 40){
                    instance.duration += 90;
                }
            }
        }
    }
}
