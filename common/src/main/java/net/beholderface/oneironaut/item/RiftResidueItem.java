package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.item.IotaHolderItem;
import net.beholderface.oneironaut.MiscAPIKt;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.casting.iotatypes.DimIota;
import net.beholderface.oneironaut.network.SpoopyScreamPacket;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;

public class RiftResidueItem extends ArbitaryDeltaPigmentItem implements IotaHolderItem {
    public RiftResidueItem(Settings settings, int[] colors, Supplier<Double> deltaGetter) {
        super(settings, colors, deltaGetter);
    }

    public int getMaxUseTime(ItemStack stack) {
        return 128;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){
        super.use(world, user, hand);
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user){
        if (!world.isClient && world != Oneironaut.getDeepNoosphere()){
            Vec3d newPos = MiscAPIKt.coerceWithinBorder(
                    MiscAPIKt.scaleBetweenDimensions(user.getPos(), world, Oneironaut.getDeepNoosphere()),
                    Oneironaut.getDeepNoosphere().getWorldBorder());
            user.teleport(Oneironaut.getDeepNoosphere(), newPos.x, 64.0, newPos.z, EnumSet.noneOf(PositionFlag.class), user.getYaw(), user.getPitch());
        }
        if ((user instanceof PlayerEntity player && !player.isCreative()) || !(user instanceof PlayerEntity)){
            stack.decrement(1);
        }
        return stack;
    }

    public UseAction getUseAction(ItemStack stack){
        return UseAction.EAT;
    }

    private static NbtCompound deepNooTag = null;
    @Override
    public @Nullable NbtCompound readIotaTag(ItemStack stack) {
        // Built from the dimension id instead of from Oneironaut.getDeepNoosphere(): that getter
        // throws whenever no server has started, and this method is reachable from the item tooltip.
        // The creative inventory builds every tooltip with a null world when it assembles its search
        // tree, so on any client without an integrated server (i.e. one on a dedicated server)
        // reaching for the ServerWorld here crashed the game.
        if (deepNooTag == null){
            deepNooTag = IotaType.serialize(new DimIota(RegistryKey.of(RegistryKeys.WORLD, Oneironaut.id("deep_noosphere"))));
        }
        return deepNooTag.copy();
    }

    @Override
    public boolean writeable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canWrite(ItemStack stack, @Nullable Iota iota) {
        return false;
    }

    @Override
    public void writeDatum(ItemStack stack, @Nullable Iota iota) {

    }

    @Override
    public void appendTooltip(ItemStack pStack, @Nullable World pLevel, List<Text> pTooltipComponents,
                              TooltipContext pIsAdvanced) {
        IotaHolderItem.appendHoverText(this, pStack, pTooltipComponents, pIsAdvanced);
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        World world = entity.getWorld();
        if (!world.isClient && world instanceof ServerWorld serverWorld){
            float pitch = 0.75f + (world.random.nextFloat() / 2);
            OneironautPlatform.sendNear(entity.getPos(), 16.0, serverWorld, new SpoopyScreamPacket(SoundEvents.ENTITY_FOX_SCREECH, pitch));
        }
    }
}
