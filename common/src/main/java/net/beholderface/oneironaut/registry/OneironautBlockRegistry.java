package net.beholderface.oneironaut.registry;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.common.lib.HexBlocks;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.beholderface.oneironaut.block.*;
import net.beholderface.oneironaut.block.blockentity.*;
import net.beholderface.oneironaut.casting.conceptmodification.ConceptModifier;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.property.BooleanProperty;
import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.util.DyeColor;
import ram.talia.hexal.common.lib.HexalBlocks;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

public class OneironautBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Oneironaut.MOD_ID, RegistryKeys.BLOCK);
    //public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Oneironaut.MOD_ID, Registry.FLUID_KEY);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Oneironaut.MOD_ID, RegistryKeys.BLOCK_ENTITY_TYPE);

    //I will not scream at my computer over this

    public static void init() {
        BLOCKS.register();
        BLOCK_ENTITIES.register();
    }
    public static final RegistrySupplier<Block> PSUEDOAMETHYST_BLOCK = BLOCKS.register("pseudoamethyst_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)
            .hardness(1.5f)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .resistance(5)
            .luminance(state -> 7)
            ));
    public static final RegistrySupplier<Block> PSUEDOAMETHYST_BLOCK_INSUBSTANTIAL = BLOCKS.register("insubstantial_pseudoamethyst_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.AMETHYST_BLOCK)
            .hardness(1f)
            .sounds(BlockSoundGroup.AMETHYST_BLOCK)
            .resistance(4)
            .luminance(state -> 5)
            .nonOpaque()
    ));
    public static final RegistrySupplier<Block> NOOSPHERE_BASALT = BLOCKS.register("noosphere_basalt", () -> new Block(AbstractBlock.Settings.copy(Blocks.BASALT)
            .hardness(1f)
            .sounds(BlockSoundGroup.BASALT)
            .resistance(4)
    ));
    public static final RegistrySupplier<NoosphereGateway> NOOSPHERE_GATE = BLOCKS.register("noosphere_gate", () -> new NoosphereGateway(AbstractBlock.Settings.copy(Blocks.END_PORTAL).luminance(state -> 15).noCollision().hardness(-1)));
    public static final RegistrySupplier<BlockEntityType<NoosphereGateEntity>> NOOSPHERE_GATE_ENTITY = BLOCK_ENTITIES.register("noosphere_gate_entity", () -> BlockEntityType.Builder.create(NoosphereGateEntity::new, NOOSPHERE_GATE.get()).build(null));
    public static final RegistrySupplier<WispLantern> WISP_LANTERN = BLOCKS.register("wisp_lantern", () -> new WispLantern(AbstractBlock.Settings.copy(Blocks.GLASS).luminance(state -> 15).sounds(BlockSoundGroup.GLASS)));
    public static final RegistrySupplier<WispLanternTinted> WISP_LANTERN_TINTED = BLOCKS.register("wisp_lantern_tinted", () -> new WispLanternTinted(AbstractBlock.Settings.copy(Blocks.GLASS).sounds(BlockSoundGroup.GLASS)));
    public static final RegistrySupplier<BlockEntityType<WispLanternEntity>> WISP_LANTERN_ENTITY = BLOCK_ENTITIES.register("wisp_lantern_entity", () -> BlockEntityType.Builder.create(WispLanternEntity::new, WISP_LANTERN.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<WispLanternEntityTinted>> WISP_LANTERN_ENTITY_TINTED = BLOCK_ENTITIES.register("wisp_lantern_entity_tinted", () -> BlockEntityType.Builder.create(WispLanternEntityTinted::new, WISP_LANTERN_TINTED.get()).build(null));
    public static final RegistrySupplier<ThoughtSlurryBlock> THOUGHT_SLURRY_BLOCK = BLOCKS.register("thought_slurry", () -> ThoughtSlurryBlock.INSTANCE);
    public static final RegistrySupplier<SuperBuddingBlock> SUPER_BUDDING = BLOCKS.register("super_budding", () -> new SuperBuddingBlock(AbstractBlock.Settings.copy(Blocks.BUDDING_AMETHYST)));
    public static final RegistrySupplier<SentinelTrapImpetus> SENTINEL_TRAP = BLOCKS.register("sentinel_trap", () -> new SentinelTrapImpetus(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK).hardness(2f)));
    public static final RegistrySupplier<BlockEntityType<SentinelTrapImpetusEntity>> SENTINEL_TRAP_ENTITY = BLOCK_ENTITIES.register("sentinel_trap_entity", () -> BlockEntityType.Builder.create(SentinelTrapImpetusEntity::new, SENTINEL_TRAP.get()).build(null));
    public static final RegistrySupplier<SentinelSensor> SENTINEL_SENSOR = BLOCKS.register("sentinel_sensor", () -> new SentinelSensor(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK)));
    public static final RegistrySupplier<BlockEntityType<SentinelSensorEntity>> SENTINEL_SENSOR_ENTITY = BLOCK_ENTITIES.register("sentinel_sensor_entity", () -> BlockEntityType.Builder.create(SentinelSensorEntity::new, SENTINEL_SENSOR.get()).build(null));
    public static final RegistrySupplier<Block> RAYCAST_BLOCKER = BLOCKS.register("raycast_blocker", () -> new Block(AbstractBlock.Settings.copy(Blocks.DEEPSLATE)));
    public static final RegistrySupplier<Block> RAYCAST_BLOCKER_GLASS = BLOCKS.register("raycast_blocker_glass", () -> new RaycastBlockerGlass(AbstractBlock.Settings.copy(Blocks.TINTED_GLASS)));
    public static final RegistrySupplier<Block> HEX_RESISTANT_BLOCK = BLOCKS.register("hex_resistant_block", () -> new Block(AbstractBlock.Settings.copy(Blocks.OBSIDIAN).hardness(1.5f)));
    public static final RegistrySupplier<Block> CIRCLE = BLOCKS.register("circle", () -> new CircleBlock(AbstractBlock.Settings.copy(Blocks.BLACK_CONCRETE)
            .nonOpaque().breakInstantly()));
    public static final RegistrySupplier<Block> MEDIA_ICE = BLOCKS.register("media_ice", ()-> new Block(AbstractBlock.Settings.copy(Blocks.PACKED_ICE)
            .slipperiness(1.1f).mapColor(MapColor.PALE_PURPLE)
    ));
    //produced by frost walker on thought slurry
    public static final RegistrySupplier<Block> MEDIA_ICE_FROSTED = BLOCKS.register("media_ice_frosted", ()-> new FrostedMediaIceBlock(AbstractBlock.Settings.copy(Blocks.PACKED_ICE)
            .slipperiness(1.08f).mapColor(MapColor.PALE_PURPLE).ticksRandomly().strength(0.5f).sounds(BlockSoundGroup.GLASS)
    ));
    public static final RegistrySupplier<MediaGelBlock> MEDIA_GEL = BLOCKS.register("media_gel", ()-> new MediaGelBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK)
            .velocityMultiplier(0.05f).jumpVelocityMultiplier(0.25f).mapColor(MapColor.PALE_PURPLE).sounds(BlockSoundGroup.SLIME).nonOpaque().hardness(Blocks.SOUL_SAND.getHardness())
    ));
    //will eventually do something related to cellular automata, and be related to the media gel
    public static final RegistrySupplier<CellBlock> CELL = BLOCKS.register("cell", ()-> new CellBlock(AbstractBlock.Settings.copy(Blocks.SLIME_BLOCK)
            .velocityMultiplier(0.6f).jumpVelocityMultiplier(0.75f).mapColor(MapColor.PALE_PURPLE).sounds(BlockSoundGroup.SLIME).nonOpaque().hardness(Blocks.SOUL_SAND.getHardness())
    ));
    public static final RegistrySupplier<BlockEntityType<CellEntity>> CELL_ENTITY = BLOCK_ENTITIES.register("cell_entity", () -> BlockEntityType.Builder.create(CellEntity::new, CELL.get()).build(null));

    public static final RegistrySupplier<WispBattery> WISP_BATTERY = BLOCKS.register("wisp_battery", ()-> new WispBattery(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK).luminance(createLightLevelFromBoolBlockState(WispBattery.REDSTONE_POWERED, 15))));
    public static final RegistrySupplier<BlockEntityType<WispBatteryEntity>> WISP_BATTERY_ENTITY = BLOCK_ENTITIES.register("wisp_battery_entity", ()-> BlockEntityType.Builder.create(WispBatteryEntity::new, WISP_BATTERY.get()).build(null));
    public static final RegistrySupplier<WispBatteryFake> WISP_BATTERY_DECORATIVE = BLOCKS.register("decorative_wisp_battery", ()-> new WispBatteryFake(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK).luminance(createLightLevelFromBoolBlockState(WispBatteryFake.REDSTONE_POWERED, 15))));
    public static final RegistrySupplier<BlockEntityType<WispBatteryEntityFake>> WISP_BATTERY_ENTITY_DECORATIVE = BLOCK_ENTITIES.register("decorative_wisp_battery_entity", ()-> BlockEntityType.Builder.create(WispBatteryEntityFake::new, WISP_BATTERY_DECORATIVE.get()).build(null));

    public static RegistrySupplier<EdifiedTreeSpawnerBlock> EDIFIED_TREE_SPAWNER = BLOCKS.register("edified_tree_spawner", ()-> new EdifiedTreeSpawnerBlock(AbstractBlock.Settings.copy(Blocks.AIR)));
    public static RegistrySupplier<BlockEntityType<EdifiedTreeSpawnerBlockEntity>> EDIFIED_TREE_SPAWNER_ENTITY = BLOCK_ENTITIES.register("edified_tree_spawner_entity", ()->BlockEntityType.Builder.create(EdifiedTreeSpawnerBlockEntity::new, EDIFIED_TREE_SPAWNER.get()).build(null));

    public static RegistrySupplier<HoverElevatorBlock> HOVER_ELEVATOR = BLOCKS.register("hover_elevator", ()-> new HoverElevatorBlock(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK).luminance(createLightLevelFromBoolBlockState(HoverElevatorBlock.POWERED, 15))));
    public static RegistrySupplier<BlockEntityType<HoverElevatorBlockEntity>> HOVER_ELEVATOR_ENTITY = BLOCK_ENTITIES.register("hover_elevator_entity", ()->BlockEntityType.Builder.create(HoverElevatorBlockEntity::new, HOVER_ELEVATOR.get()).build(null));
    public static RegistrySupplier<Block> HOVER_REPEATER = BLOCKS.register("hover_repeater", ()->new HoverRepeaterBlock(AbstractBlock.Settings.create().mapColor(MapColor.DEEPSLATE_GRAY).instrument(Instrument.BASEDRUM).strength(3.0F, 6.0F).sounds(BlockSoundGroup.DEEPSLATE).breakInstantly().nonOpaque().noCollision()));

    public static RegistrySupplier<AmethystClusterBlock> PSEUDOAMETHYST_CLUSTER = BLOCKS.register("pseudoamethyst_cluster", ()-> new AmethystClusterBlock(7, 3, AbstractBlock.Settings.copy(Blocks.AMETHYST_CLUSTER)));
    public static RegistrySupplier<AmethystClusterBlock> PSEUDOAMETHYST_BUD_LARGE = BLOCKS.register("pseudoamethyst_bud_large", ()-> new AmethystClusterBlock(5, 3, AbstractBlock.Settings.copy(Blocks.LARGE_AMETHYST_BUD)));
    public static RegistrySupplier<AmethystClusterBlock> PSEUDOAMETHYST_BUD_MEDIUM = BLOCKS.register("pseudoamethyst_bud_medium", ()-> new AmethystClusterBlock(4, 3, AbstractBlock.Settings.copy(Blocks.MEDIUM_AMETHYST_BUD)));
    public static RegistrySupplier<AmethystClusterBlock> PSEUDOAMETHYST_BUD_SMALL = BLOCKS.register("pseudoamethyst_bud_small", ()-> new AmethystClusterBlock(3, 4, AbstractBlock.Settings.copy(Blocks.SMALL_AMETHYST_BUD)));

    public static RegistrySupplier<SpaceBombBlock> SPACE_BOMB = BLOCKS.register("spacebomb", ()->new SpaceBombBlock(AbstractBlock.Settings.copy(Blocks.RESPAWN_ANCHOR).luminance(createLightLevelFromBoolBlockState(BlockSlate.ENERGIZED, 13))));
    public static RegistrySupplier<BlockEntityType<SpaceBombBlockEntity>> SPACE_BOMB_ENTITY = BLOCK_ENTITIES.register("spacebomb_entity", ()->BlockEntityType.Builder.create(SpaceBombBlockEntity::new, SPACE_BOMB.get()).build(null));

    public static RegistrySupplier<InactiveSlipwayBlock> INACTIVE_SLIPWAY = BLOCKS.register("inactiveslipway", ()->new InactiveSlipwayBlock(AbstractBlock.Settings.copy(HexalBlocks.SLIPWAY)));
    public static RegistrySupplier<SlipwaySuppressorBlock> SLIPWAY_SUPPRESSOR = BLOCKS.register("slipwaysuppressor", ()->new SlipwaySuppressorBlock(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK)));

    //not intended to be the real-world monk fruit, just thought it was a good name, especially considering the etymology (https://en.wikipedia.org/wiki/Siraitia_grosvenorii#Etymology_and_regional_names)
    public static RegistrySupplier<RenderBerryBushBlock> RENDER_BUSH = BLOCKS.register("monkfruit_bush", ()->new RenderBerryBushBlock(AbstractBlock.Settings.copy(Blocks.SWEET_BERRY_BUSH)));

    public static RegistrySupplier<ExtradimensionalBoundaryLocus> EXTRADIM_LOCUS = BLOCKS.register("extradimensional_border", ()->new ExtradimensionalBoundaryLocus(AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK).luminance(createLightLevelFromBoolBlockState(BlockCircleComponent.ENERGIZED, 14))));

    //yes it acts like an xray thingy, no I don't care, it's not available in survival
    public static RegistrySupplier<DeepNoosphereFloorBlock> DEEP_NOOSPHERE_FLOOR = BLOCKS.register("deep_border", ()->new DeepNoosphereFloorBlock(AbstractBlock.Settings.copy(Blocks.BEDROCK).noBlockBreakParticles().mapColor(MapColor.PALE_PURPLE)));

    public static Supplier<AbstractBlock.Settings> CONCEPT_MODIFIER_SETTINGS = ()->AbstractBlock.Settings.copy(HexBlocks.SLATE_BLOCK).luminance((state)->15);

    public static RegistrySupplier<ConceptCoreBlock> CONCEPT_CORE = BLOCKS.register("concept_core", ()->new ConceptCoreBlock(CONCEPT_MODIFIER_SETTINGS.get()));
    public static RegistrySupplier<BlockEntityType<ConceptCoreBlockEntity>> CONCEPT_CORE_ENTITY = BLOCK_ENTITIES.register("concept_core_entity", ()->BlockEntityType.Builder.create(ConceptCoreBlockEntity::new, CONCEPT_CORE.get()).build(null));

    public static RegistrySupplier<ConceptConnectorBlock> CONCEPT_CONNECTOR = BLOCKS.register("concept_connector", ()->new ConceptConnectorBlock(CONCEPT_MODIFIER_SETTINGS.get()));

    public static RegistrySupplier<ConceptDecoratorBlock> CONCEPT_MODIFIER_EMPTY = BLOCKS.register("concept_modifier_empty", ()->new ConceptDecoratorBlock(CONCEPT_MODIFIER_SETTINGS.get()));
    public static RegistrySupplier<ConceptDecoratorBlock> CONCEPT_MODIFIER_SUS = BLOCKS.register("concept_modifier_sus", ()->new ConceptDecoratorBlock(CONCEPT_MODIFIER_SETTINGS.get()));

    public static final Map<DyeColor, RegistrySupplier<ConceptDecoratorBlock>> COLORFUL_CONCEPT_MODIFIERS = new HashMap<>();
    static {
        for (DyeColor color : DyeColor.values()){
            RegistrySupplier<ConceptDecoratorBlock> supplier = BLOCKS.register("concept_decorator_color/" + color.getName(), ()->new ConceptDecoratorBlock(CONCEPT_MODIFIER_SETTINGS.get()));
            COLORFUL_CONCEPT_MODIFIERS.put(color, supplier);
        }
    }

    private static final Function<NbtCompound, Double> ATTRIBUTE_CONCEPT_CALULATOR = (nbt)->{
        double potency = nbt.getDouble("potency");
        if (potency <= 1){
            potency = (1 - potency) + 1; //0.8 original potency becomes 1.2 processed potency
            return -Math.pow(potency * 5.0, 2.0);
        } else {
            return Math.pow(potency * 10.0, 2.0);
        }
    };
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_GRIDSIZE = BLOCKS.register("concept_modifier_gridsize", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.ATTRIBUTE, HexAttributes.GRID_ZOOM, ATTRIBUTE_CONCEPT_CALULATOR));
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_MAXHEALTH = BLOCKS.register("concept_modifier_maxhealth", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.ATTRIBUTE, EntityAttributes.GENERIC_MAX_HEALTH, ATTRIBUTE_CONCEPT_CALULATOR));
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_ANTIEROSION = BLOCKS.register("concept_modifier_antierosion", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.ANTIEROSION, (nbt)->10000.0));
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_REFERENCE_FALSY = BLOCKS.register("concept_modifier_falsy", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.FALSY_REFERENCE, (nbt)->1000.0));
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_REFERENCE_COMPARISON = BLOCKS.register("concept_modifier_comparison", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.REFERENCE_COMPARISON, (nbt)->1000.0));
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_GTP_DROP = BLOCKS.register("concept_modifier_gtp_drop", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.GTP_DROPREDUCTION, (nbt)->1000.0));
    public static RegistrySupplier<ConceptModifierBlock> CONCEPT_MODIFIER_STACK_SIZE = BLOCKS.register("concept_modifier_stack_size", ()->new ConceptModifierBlock(CONCEPT_MODIFIER_SETTINGS.get(), ConceptModifier.ModifierType.STACK_LIMIT, (nbt)->1000.0));

    public static RegistrySupplier<BlockEntityType<ConceptModifierBlockEntity>> CONCEPT_MODIFIER_ENTITY = BLOCK_ENTITIES.register("concept_modifier_entity", ()->BlockEntityType.Builder.create(ConceptModifierBlockEntity::new,
            CONCEPT_MODIFIER_GRIDSIZE.get(), CONCEPT_MODIFIER_MAXHEALTH.get(), CONCEPT_MODIFIER_GTP_DROP.get(),
            CONCEPT_MODIFIER_ANTIEROSION.get(), CONCEPT_MODIFIER_REFERENCE_FALSY.get(), CONCEPT_MODIFIER_REFERENCE_COMPARISON.get(),
            CONCEPT_MODIFIER_STACK_SIZE.get()
    ).build(null));

    public static RegistrySupplier<TranformingSkullBlock> TRANFORMING_SKULL = BLOCKS.register("transformingskull", ()->new TranformingSkullBlock(AbstractBlock.Settings.copy(Blocks.ZOMBIE_HEAD)));
    public static RegistrySupplier<TranformingWallSkullBlock> TRANFORMING_WALL_SKULL = BLOCKS.register("transformingskull_wall", ()->new TranformingWallSkullBlock(AbstractBlock.Settings.copy(Blocks.ZOMBIE_HEAD)));
    public static RegistrySupplier<BlockEntityType<TransformingSkullBlockEntity>> TRANFORMING_SKULL_ENTITY = BLOCK_ENTITIES.register("transformingskull_entity", ()-> BlockEntityType.Builder.create(TransformingSkullBlockEntity::new, TRANFORMING_SKULL.get(), TRANFORMING_WALL_SKULL.get()).build(null));

    public static final AbstractBlock.Settings INSTANT_BREAKER_SETTINGS = AbstractBlock.Settings.create().strength(-1.0F, 3600000.8F).nonOpaque().allowsSpawning((state, world, pos, entityType) -> false).noBlockBreakParticles().pistonBehavior(PistonBehavior.BLOCK).noCollision().ticksRandomly();
    public static RegistrySupplier<InstantBreakingBlock> INSTANT_BREAKER_RIFTRESIDUE = BLOCKS.register("rift_residue", ()->new InstantBreakingBlock(INSTANT_BREAKER_SETTINGS));
    public static RegistrySupplier<BlockEntityType<InstantBreakingBlockEntity>> INSTANT_BREAKER_ENTITY = BLOCK_ENTITIES.register("instant_breaker_entity", ()-> BlockEntityType.Builder.create(InstantBreakingBlockEntity::new, INSTANT_BREAKER_RIFTRESIDUE.get()).build(null));


    //mostly just stolen from the vanilla class since it's private in there
    protected static ToIntFunction<BlockState> createLightLevelFromBoolBlockState(BooleanProperty property, int litLevel) {
        return state -> state.get(property) ? litLevel : 0;
    }


    //used for the eternal chorus mixin
    public static final BooleanProperty ETERNAL = BooleanProperty.of("eternal");
}
