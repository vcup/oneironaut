package net.beholderface.oneironaut.fabric;

import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.beholderface.oneironaut.registry.OneironautComponents;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

/**
 * Fabric implementation of {@link OneironautPlatform.Loader}.
 * <p>
 * On Fabric every {@code IMessage} can travel through Hex Casting's xplat layer, because that
 * implementation keys off {@link IMessage#getFabricId()} rather than a fixed channel registry, so
 * this is a thin forwarding layer that preserves the mod's original Fabric behaviour exactly.
 */
public class FabricPlatform implements OneironautPlatform.Loader {

    @Override
    public void sendToPlayer(ServerPlayerEntity player, IMessage message) {
        IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, message);
    }

    @Override
    public void sendNear(Vec3d pos, double radius, ServerWorld world, IMessage message) {
        IXplatAbstractions.INSTANCE.sendPacketNear(pos, radius, world, message);
    }

    @Override
    public Entity teleport(Entity entity, ServerWorld destination, TeleportTarget target) {
        return FabricDimensions.teleport(entity, destination, target);
    }

    @Override
    public void clearBrainsweep(MobEntity mob) {
        HexCardinalComponents.BRAINSWEPT.get(mob).setBrainswept(false);
    }

    @Override
    public boolean isDecorativeWisp(Entity wisp) {
        return OneironautComponents.WISP_DECORATIVE.get(wisp).getValue();
    }

    @Override
    public void setDecorativeWisp(Entity wisp, boolean decorative) {
        OneironautComponents.WISP_DECORATIVE.get(wisp).setValue(decorative);
    }
}
