package net.beholderface.oneironaut.neoforge;

import dev.architectury.platform.forge.EventBuses;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.neoforge.net.NeoForgePacketHandler;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * NeoForge loading entrypoint.
 * <p>
 * On Minecraft 1.20.1 NeoForge is a fork of Forge that keeps the {@code net.minecraftforge.*} package
 * names and the {@code forge} mod id, so this class is written against the Forge API. What makes it a
 * NeoForge build rather than a Forge one is the loader artifact it is compiled against
 * ({@code net.neoforged:forge}) and the dependency ranges in {@code META-INF/mods.toml}.
 */
@Mod(Oneironaut.MOD_ID)
public class OneironautNeoForge {
    public OneironautNeoForge() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the packet channel first: NeoForgePlatform routes Oneironaut's own messages through
        // it, so the channel must already know its message types before the seam can be used.
        NeoForgePacketHandler.init();
        // Install the loader half of the platform seam before anything in common/ runs.
        OneironautPlatform.install(new NeoForgePlatform());
        NeoForgeFluidInit.init(bus);
        NeoForgeOneironautConfig.setup();

        bus.addListener(OneironautClientNeoForge::init);
        // Hex Casting's own registries are created from NewRegistryEvent, which fires after mod
        // construction, so the half of the initialisation that touches them has to wait for common setup.
        bus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(Oneironaut::initLate));

        // Submit our event bus to let architectury register our content on the right time.
        EventBuses.registerModEventBus(Oneironaut.MOD_ID, bus);
        Oneironaut.initEarly();
    }
}
