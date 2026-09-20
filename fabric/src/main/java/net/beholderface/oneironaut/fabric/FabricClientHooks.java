package net.beholderface.oneironaut.fabric;

import net.beholderface.oneironaut.platform.OneironautClientHooks;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.mixin.client.rendering.DimensionEffectsAccessor;
import net.minecraft.block.Block;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

/**
 * Fabric implementation of {@link OneironautClientHooks.Client}. This is exactly the Fabric API code
 * the mod used to call from shared code, moved behind the platform seam so NeoForge can supply its own.
 */
public class FabricClientHooks implements OneironautClientHooks.Client {

    @Override
    public void putBlockRenderLayer(Block block, RenderLayer layer) {
        BlockRenderLayerMap.INSTANCE.putBlock(block, layer);
    }

    @Override
    public void putFluidRenderLayer(RenderLayer layer, Fluid still, Fluid flowing) {
        BlockRenderLayerMap.INSTANCE.putFluids(layer, still, flowing);
    }

    @Override
    public void registerDimensionEffects(Identifier id, Supplier<DimensionEffects> effects) {
        DimensionEffectsAccessor.getIdentifierMap().put(id, effects.get());
    }
}
