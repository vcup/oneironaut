package net.beholderface.oneironaut.status;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.Mishap;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.lib.HexDamageTypes;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.oneironaut.OneironautDamage;
import net.beholderface.oneironaut.casting.environments.ForcedMediaCostEnv;
import net.beholderface.oneironaut.item.BottomlessMediaItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class DetectionResistEffect extends StatusEffect {
    public DetectionResistEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xcfa0f3);
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier){
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier){
        long time = entity.getWorld().getTime();
        if (!(entity.getWorld().isClient)){
            ItemStack mainStack = entity.getMainHandStack();
            ItemStack offStack = entity.getOffHandStack();
            if ((time % 5) == 0){
                ((ServerWorld) entity.getWorld()).playSoundFromEntity(
                        null, entity, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, entity.getSoundCategory(), 1.5f, 1f);
            }
            if (amplifier < 100){ //lets people like modpack devs and server admins give things the effect without hurting them
                if (entity.isPlayer()){
                    if ((time % 20) == 0){
                        ServerPlayerEntity player = (ServerPlayerEntity) entity;
                        CastingEnvironment env = new ForcedMediaCostEnv(player, Hand.MAIN_HAND);
                        long deficit = env.extractMedia(MediaConstants.DUST_UNIT / 10, false);
                        if (deficit > 0 && (time % 40) == 0){
                            Mishap.Companion.trulyHurt(entity, OneironautDamage.overcast(entity), 1f);
                        }
                    }
                } else if (entity instanceof MobEntity){
                    if (mainStack.getItem() instanceof BottomlessMediaItem || offStack.getItem() instanceof BottomlessMediaItem || IXplatAbstractions.INSTANCE.isBrainswept((MobEntity) entity)){
                        //do nothing, they are immune
                    } else if ((time % 40) == 0) {
                        Mishap.Companion.trulyHurt(entity, OneironautDamage.overcast(entity), 1f);
                    }
                } else if ((time % 40) == 0){
                    Mishap.Companion.trulyHurt(entity, OneironautDamage.overcast(entity), 1f);
                }
            }
        }
    }
}