package net.beholderface.oneironaut.neoforge.mixin;

import net.beholderface.oneironaut.block.ThoughtSlurry;
import net.beholderface.oneironaut.neoforge.NeoForgeFluidInit;
import net.minecraftforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Gives the shared {@link ThoughtSlurry} fluid its Forge/NeoForge {@link FluidType}.
 * <p>
 * Forge requires every {@code Fluid} to report a {@code FluidType}, and {@code Fluid#getFluidType()}
 * defaults to the empty type. The shared class cannot declare the override itself because it is
 * compiled against Fabric, so the NeoForge module adds it here. Fabric does not need this: it draws
 * the fluid through the render handler registered in {@code FabricClientHooks}.
 */
@Mixin(ThoughtSlurry.class)
public abstract class ThoughtSlurryFluidTypeMixin {

    public FluidType getFluidType() {
        return NeoForgeFluidInit.THOUGHT_SLURRY_TYPE.get();
    }
}
