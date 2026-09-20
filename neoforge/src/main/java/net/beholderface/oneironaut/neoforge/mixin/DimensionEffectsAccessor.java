package net.beholderface.oneironaut.neoforge.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes the vanilla map of dimension effects so Oneironaut can register the sky and fog effects for
 * the noosphere and deep noosphere.
 * <p>
 * Fabric API ships its own accessor for this; NeoForge has no equivalent, so the mod brings its own.
 * The field is a fastutil {@code Object2ObjectMap}, and the accessor's return type has to say so or
 * Mixin cannot match the target.
 */
@Mixin(DimensionEffects.class)
public interface DimensionEffectsAccessor {
    @Accessor("BY_IDENTIFIER")
    static Object2ObjectMap<Identifier, DimensionEffects> getIdentifierMap() {
        throw new AssertionError();
    }
}
