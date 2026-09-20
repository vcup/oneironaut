package net.beholderface.oneironaut.neoforge;

import at.petrak.hexcasting.common.msgs.IMessage;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import net.beholderface.oneironaut.neoforge.net.DecorativeWispPacket;
import net.beholderface.oneironaut.neoforge.net.NeoForgePacketHandler;
import net.beholderface.oneironaut.platform.OneironautPlatform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;

/**
 * NeoForge implementation of {@link OneironautPlatform.Loader}.
 */
public class NeoForgePlatform implements OneironautPlatform.Loader {

    /**
     * Hexal registers its own messages on its own Forge channel, so Hex Casting's channel cannot
     * carry them. On Fabric both mods key off {@code IMessage#getFabricId()} and it makes no
     * difference, which is why the shared code used to send hexal's messages through Hex.
     * <p>
     * The namespace of the message id is what actually identifies the owning channel, so route on
     * that rather than on a class-name prefix.
     */
    private static boolean isHexalMessage(IMessage message) {
        return "hexal".equals(message.getFabricId().getNamespace());
    }

    @Override
    public void sendToPlayer(ServerPlayerEntity player, IMessage message) {
        if (NeoForgePacketHandler.owns(message)) {
            NeoForgePacketHandler.sendToPlayer(player, message);
        } else if (isHexalMessage(message)) {
            ram.talia.hexal.xplat.IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, message);
        } else {
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, message);
        }
    }

    @Override
    public void sendNear(Vec3d pos, double radius, ServerWorld world, IMessage message) {
        if (NeoForgePacketHandler.owns(message)) {
            NeoForgePacketHandler.sendNear(pos, radius, world, message);
        } else if (isHexalMessage(message)) {
            ram.talia.hexal.xplat.IXplatAbstractions.INSTANCE.sendPacketNear(pos, radius, world, message);
        } else {
            IXplatAbstractions.INSTANCE.sendPacketNear(pos, radius, world, message);
        }
    }

    @Override
    public Entity teleport(Entity entity, ServerWorld destination, TeleportTarget target) {
        return entity.changeDimension(destination, new OneironautTeleporter(target));
    }

    @Override
    public void clearBrainsweep(MobEntity mob) {
        // Hex Casting keeps this flag in the mob's persistent data on Forge/NeoForge, so clear that.
        // Deliberately do NOT send Hex's MsgBrainsweepAck here: its client handler calls
        // setBrainsweepAddlData, which puts the flag back to true, i.e. the exact opposite of what
        // this method is for. Clients are told about the clear by Oneironaut's own
        // UnBrainsweepPacket, which MiscAPI's unbrainsweep() sends alongside this call.
        mob.getPersistentData().remove("hexcasting:brainswept");
    }

    @Override
    public boolean isDecorativeWisp(Entity wisp) {
        return DecorativeWispFlags.isDecorative(wisp);
    }

    @Override
    public void setDecorativeWisp(Entity wisp, boolean decorative) {
        DecorativeWispFlags.setDecorative(wisp, decorative);
    }
}
