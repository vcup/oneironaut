package net.beholderface.oneironaut.neoforge.mixin;

import at.petrak.hexcasting.forge.xplat.ForgeXplatImpl;
import net.beholderface.oneironaut.registry.OneironautTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps blocks tagged {@code oneironaut:break_immune} safe from Hex Casting's break spell.
 * <p>
 * Hex Casting's break check lives in a loader-specific xplat class, so this has to exist once per
 * loader: the Fabric twin injects into {@code FabricXplatImpl}, this one into {@code ForgeXplatImpl}
 * (which NeoForge 1.20.1 also uses).
 */
@Mixin(ForgeXplatImpl.class)
public abstract class OpBreakBlockImmunityMixin {

    @Inject(method = "isBreakingAllowed", at = @At(value = "HEAD", remap = false), remap = false, cancellable = true)
    public void dontBreakIfImmune(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity player,
                                  CallbackInfoReturnable<Boolean> cir) {
        if (state.isIn(OneironautTags.Blocks.breakImmune)) {
            cir.setReturnValue(false);
        }
    }
}
