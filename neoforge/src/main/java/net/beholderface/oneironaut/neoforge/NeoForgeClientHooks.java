package net.beholderface.oneironaut.neoforge;

import net.beholderface.oneironaut.neoforge.mixin.DimensionEffectsAccessor;
import net.beholderface.oneironaut.platform.OneironautClientHooks;
import net.minecraft.block.Block;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * NeoForge implementation of {@link OneironautClientHooks.Client}.
 * <p>
 * Fluid textures and tint are not registered here: they belong to the fluid's {@code FluidType} client
 * extensions, which {@link NeoForgeFluidInit} installs, and the values come from the shared
 * {@link net.beholderface.oneironaut.block.ThoughtSlurryAppearance}.
 */
public class NeoForgeClientHooks implements OneironautClientHooks.Client {

    @Override
    public void putBlockRenderLayer(Block block, RenderLayer layer) {
        RenderLayers.setRenderLayer(block, layer);
    }

    @Override
    public void putFluidRenderLayer(RenderLayer layer, Fluid still, Fluid flowing) {
        // Forge's RenderLayers.setRenderLayer(Fluid, RenderLayer) writes into its own
        // FLUID_RENDER_TYPES map (keyed by registry delegate), which is what getFluidLayer reads, so
        // this is the real thing rather than a no-op. It must run while the client is still loading
        // (setRenderLayer checks ClientModLoader.isLoading()); the client setup event satisfies that.
        RenderLayers.setRenderLayer(still, layer);
        RenderLayers.setRenderLayer(flowing, layer);
    }

    @Override
    public void registerDimensionEffects(Identifier id, Supplier<DimensionEffects> effects) {
        DimensionEffectsAccessor.getIdentifierMap().put(id, effects.get());
    }
}
