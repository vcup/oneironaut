package net.beholderface.oneironaut.mixin;

import at.petrak.hexcasting.api.misc.MediaConstants;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ram.talia.hexal.common.entities.WanderingWisp;

@Mixin(WanderingWisp.class)
public class DecorativeWispMixin {
    @Unique
    WanderingWisp wisp = (WanderingWisp) (Object) this;

    @Inject(method = "getMedia()J", at = @At(value = "HEAD", remap = false), cancellable = true, remap = false)
    public void nomedia(CallbackInfoReturnable<Long> cir){
        //thank you [
        if (OneironautPlatform.isDecorativeWisp(wisp)){
            //approximately net-zero media from consuming it rather than just eating a shard for no media
            cir.setReturnValue(MediaConstants.SHARD_UNIT);
        }
    }
}
