package net.beholderface.oneironaut.block;

import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.util.Identifier;

/**
 * How thought slurry is drawn, in loader-neutral terms: the two animated textures and the colour they
 * are tinted with. Both the textures and the tint are greyscale, so <em>all</em> of the fluid's colour
 * comes from {@link #TINT}.
 * <p>
 * This class exists so Fabric and NeoForge cannot drift apart: Fabric hands these values to
 * {@code SimpleFluidRenderHandler}, NeoForge returns them from its {@code IClientFluidTypeExtensions}.
 * Previously each side hardcoded its own copy, and the NeoForge copy alone was wrong.
 */
public final class ThoughtSlurryAppearance {
    public static final Identifier STILL_TEXTURE = Oneironaut.id("block/thought_slurry");
    public static final Identifier FLOWING_TEXTURE = Oneironaut.id("block/thought_slurry_flowing");

    /**
     * The tint applied to both textures, as ARGB.
     * <p>
     * The alpha channel is <em>not</em> decorative and must stay {@code 0xFF}. Vanilla's fluid renderer
     * (and so Fabric, which feeds this value into it) unpacks only red, green and blue and hardcodes an
     * alpha of 1; Forge/NeoForge's {@code LiquidBlockRenderer} patch unpacks the alpha channel as well.
     * A tint written as {@code 0x8621C2} -- alpha {@code 0x00} -- therefore looked correct on Fabric and
     * was tessellated fully transparent on NeoForge, i.e. the fluid was invisible.
     */
    public static final int TINT = 0xFF8621C2;

    private ThoughtSlurryAppearance() {
    }
}
