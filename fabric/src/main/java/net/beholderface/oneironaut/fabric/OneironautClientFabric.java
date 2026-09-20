package net.beholderface.oneironaut.fabric;

import net.beholderface.oneironaut.block.ThoughtSlurry;
import net.beholderface.oneironaut.block.ThoughtSlurryAppearance;
import net.fabricmc.api.ClientModInitializer;
import net.beholderface.oneironaut.OneironautClient;
import net.beholderface.oneironaut.platform.OneironautClientHooks;
import net.beholderface.oneironaut.fabric.FabricPacketHandler.*;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;

/**
 * Fabric client loading entrypoint.
 */
public class OneironautClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OneironautClientHooks.install(new FabricClientHooks());
        // Fabric's way of describing a fluid's appearance. NeoForge does the same thing from the
        // FluidType it registers; both read the values from ThoughtSlurryAppearance.
        FluidRenderHandlerRegistry.INSTANCE.register(ThoughtSlurry.STILL_FLUID, ThoughtSlurry.FLOWING_FLUID,
                new SimpleFluidRenderHandler(ThoughtSlurryAppearance.STILL_TEXTURE,
                        ThoughtSlurryAppearance.FLOWING_TEXTURE, ThoughtSlurryAppearance.TINT));
        OneironautClient.init();
        FabricPacketHandler.INSTANCE.initClientBound();
    }
}
