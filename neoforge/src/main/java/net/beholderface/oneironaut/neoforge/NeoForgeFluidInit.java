package net.beholderface.oneironaut.neoforge;

import net.beholderface.oneironaut.Oneironaut;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

/**
 * Registers the Forge/NeoForge {@link FluidType} for Oneironaut's thought slurry.
 * <p>
 * The {@code Fluid} objects themselves are still registered by the shared Architectury registry in
 * {@code OneironautMiscRegistry}; only the fluid type is loader-specific, and
 * {@code ThoughtSlurryFluidTypeMixin} attaches this type to that fluid. Registering a second
 * still/flowing pair, block or bucket here (as the old half-finished Forge module did) would collide
 * with the shared registrations under the same names.
 */
public class NeoForgeFluidInit {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Oneironaut.MOD_ID);

    public static final RegistryObject<FluidType> THOUGHT_SLURRY_TYPE = FLUID_TYPES.register("thought_slurry",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("fluid.oneironaut.thought_slurry")
                    .canPushEntity(true)
                    .canSwim(true)
                    .canDrown(true)
                    .canExtinguish(true)
                    .supportsBoating(true)) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new ThoughtSlurryClientExtensions());
                }
            });

    private NeoForgeFluidInit() {
    }

    public static void init(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
    }
}
