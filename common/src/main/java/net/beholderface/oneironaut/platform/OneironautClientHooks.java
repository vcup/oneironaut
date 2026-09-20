package net.beholderface.oneironaut.platform;

import net.minecraft.block.Block;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * Client-only half of the platform seam, installed by each loader's client entrypoint.
 * <p>
 * Render layers and dimension effects are registered differently on Fabric and on NeoForge, so shared
 * client code talks to this instead of to Fabric API directly.
 * <p>
 * A fluid's textures and tint are deliberately <em>not</em> here. Fabric applies them through a Fabric
 * API registry, while NeoForge has to hand them to the fluid's {@code FluidType} when the type is
 * registered -- long before {@code OneironautClient.init()} runs. The values therefore live in
 * {@link net.beholderface.oneironaut.block.ThoughtSlurryAppearance}, and each loader's own client
 * entrypoint passes them to its own API. Anything else would leave NeoForge implementing an empty
 * method.
 */
public final class OneironautClientHooks {
    private OneironautClientHooks() {
    }

    public interface Client {
        /** Puts a block on the given render layer. */
        void putBlockRenderLayer(Block block, RenderLayer layer);

        /** Puts a still/flowing fluid pair on the given render layer. */
        void putFluidRenderLayer(RenderLayer layer, Fluid still, Fluid flowing);

        /** Registers the sky/fog effects used by one of Oneironaut's dimensions. */
        void registerDimensionEffects(Identifier id, Supplier<DimensionEffects> effects);
    }

    private static volatile Client client;

    public static void install(Client client) {
        OneironautClientHooks.client = client;
    }

    private static Client client() {
        Client current = client;
        if (current == null) {
            throw new IllegalStateException("Oneironaut's client hooks were never installed; the "
                    + "platform client entrypoint must call OneironautClientHooks.install(...)");
        }
        return current;
    }

    public static void putBlockRenderLayer(Block block, RenderLayer layer) {
        client().putBlockRenderLayer(block, layer);
    }

    public static void putFluidRenderLayer(RenderLayer layer, Fluid still, Fluid flowing) {
        client().putFluidRenderLayer(layer, still, flowing);
    }

    public static void registerDimensionEffects(Identifier id, Supplier<DimensionEffects> effects) {
        client().registerDimensionEffects(id, effects);
    }
}
