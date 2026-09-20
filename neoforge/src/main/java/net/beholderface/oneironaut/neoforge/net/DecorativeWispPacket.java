package net.beholderface.oneironaut.neoforge.net;

import at.petrak.hexcasting.common.msgs.IMessage;
import io.netty.buffer.ByteBuf;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.neoforge.DecorativeWispFlags;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

/**
 * Tells clients that a wisp has (or no longer has) Oneironaut's decorative marker.
 * <p>
 * On Fabric this flag lives in a Cardinal Components entity component that syncs itself. Cardinal
 * Components' Fabric build is not available to a native NeoForge mod (the hexisle pack only ships it
 * for Sinytra Connector), so NeoForge carries the flag itself and syncs it with this packet.
 */
public class DecorativeWispPacket implements IMessage {
    private final int entityId;
    private final boolean decorative;

    public DecorativeWispPacket(int entityId, boolean decorative) {
        this.entityId = entityId;
        this.decorative = decorative;
    }

    @Override
    public void serialize(PacketByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(decorative);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    public static final Identifier ID = new Identifier(Oneironaut.MOD_ID, "decorativewisp");

    public static DecorativeWispPacket deserialise(ByteBuf buffer) {
        PacketByteBuf buf = new PacketByteBuf(buffer);
        return new DecorativeWispPacket(buf.readInt(), buf.readBoolean());
    }

    public static void handle(DecorativeWispPacket self) {
        DecorativeWispFlags.setClientSide(self.entityId, self.decorative);
    }
}
