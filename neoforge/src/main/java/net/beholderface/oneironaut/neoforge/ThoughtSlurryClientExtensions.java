package net.beholderface.oneironaut.neoforge;

import com.mojang.blaze3d.systems.RenderSystem;
import net.beholderface.oneironaut.block.ThoughtSlurryAppearance;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.FogShape;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

/**
 * Client-side appearance of thought slurry on NeoForge: the Forge/NeoForge counterpart of the Fabric
 * render handler the Fabric module registers. Textures and tint come from
 * {@link ThoughtSlurryAppearance}, so the two platforms cannot disagree about colour.
 * <p>
 * This is a separate class so that no client-only type appears in {@link NeoForgeFluidInit}'s own
 * signatures, which keeps that class safe to load on a dedicated server.
 * <p>
 * <b>Deliberate NeoForge-only extras:</b> the fog ({@link #modifyFogColor}, {@link #modifyFogRender})
 * and the {@code water_overlay} surface overlay. Both were carried over from the mod's own Forge
 * modules -- 1.19.2's {@code forge/FluidRegistryContainer} sets exactly these fog distances, and its
 * {@code ClientExtensions} pairs every fluid with an overlay -- so they are behaviour the Forge side of
 * this mod always had, not something this port invented. The Fabric build has no equivalent, and there
 * is no way to express either through {@code SimpleFluidRenderHandler}. Remove both overrides if strict
 * cross-platform parity is wanted instead.
 */
public class ThoughtSlurryClientExtensions implements IClientFluidTypeExtensions {
    private static final float FOG_RED = 0.678F;
    private static final float FOG_GREEN = 0.451F;
    private static final float FOG_BLUE = 0.808F;

    @Override
    public Identifier getStillTexture() {
        return ThoughtSlurryAppearance.STILL_TEXTURE;
    }

    @Override
    public Identifier getFlowingTexture() {
        return ThoughtSlurryAppearance.FLOWING_TEXTURE;
    }

    @Override
    public Identifier getOverlayTexture() {
        return new Identifier("minecraft", "block/water_overlay");
    }

    @Override
    public int getTintColor() {
        return ThoughtSlurryAppearance.TINT;
    }

    @Override
    public int getTintColor(FluidState state, BlockRenderView getter, BlockPos pos) {
        return ThoughtSlurryAppearance.TINT;
    }

    @Override
    public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientWorld level, int renderDistance,
                                            float darkenWorldAmount, Vector3f fluidFogColor) {
        // A fresh vector per call: the caller is free to mutate what it is handed, so returning a shared
        // constant would let one frame's fog settings leak into the next.
        return new Vector3f(FOG_RED, FOG_GREEN, FOG_BLUE);
    }

    @Override
    public void modifyFogRender(Camera camera, BackgroundRenderer.FogType mode, float renderDistance, float partialTick,
                                float nearDistance, float farDistance, FogShape shape) {
        RenderSystem.setShaderFogStart(1f);
        RenderSystem.setShaderFogEnd(6f);
    }
}
