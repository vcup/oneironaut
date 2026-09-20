package net.beholderface.oneironaut.neoforge.net;

import at.petrak.hexcasting.common.msgs.IMessage;
import net.beholderface.oneironaut.Oneironaut;
import net.beholderface.oneironaut.network.FireballUpdatePacket;
import net.beholderface.oneironaut.network.HoverliftAntiDesyncPacket;
import net.beholderface.oneironaut.network.ItemUpdatePacket;
import net.beholderface.oneironaut.network.ParticleBurstPacket;
import net.beholderface.oneironaut.network.SpoopyScreamPacket;
import net.beholderface.oneironaut.network.UnBrainsweepPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Oneironaut's own network channel for NeoForge.
 * <p>
 * This exists because Hex Casting's Forge implementation routes {@code IMessage}s through its own
 * {@code SimpleChannel}, which only has Hex's message classes registered. Handing it one of
 * Oneironaut's messages throws {@code Unregistered message class}. Hex Casting's Fabric
 * implementation instead keys off {@link IMessage#getFabricId()}, which is why the shared code could
 * get away with sending everything through {@code IXplatAbstractions} on Fabric.
 * <p>
 * Payloads are byte-for-byte the same as Fabric's: the encoder reuses {@link IMessage#toBuf()} and the
 * decoders reuse each packet's own {@code deserialise}, so both loaders speak the same wire format.
 */
public final class NeoForgePacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final Set<Class<?>> OWNED_MESSAGES = new HashSet<>();

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new Identifier(Oneironaut.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private NeoForgePacketHandler() {
    }

    public static void init() {
        int id = 0;
        id = register(id, ParticleBurstPacket.class, ParticleBurstPacket::deserialise, ParticleBurstPacket::handle);
        id = register(id, FireballUpdatePacket.class, FireballUpdatePacket::deserialise, FireballUpdatePacket::handle);
        id = register(id, ItemUpdatePacket.class, ItemUpdatePacket::deserialise, ItemUpdatePacket::handle);
        id = register(id, UnBrainsweepPacket.class, UnBrainsweepPacket::deserialise, UnBrainsweepPacket::handle);
        id = register(id, SpoopyScreamPacket.class, SpoopyScreamPacket::deserialise, SpoopyScreamPacket::handle);
        id = register(id, HoverliftAntiDesyncPacket.class, HoverliftAntiDesyncPacket::deserialise,
                HoverliftAntiDesyncPacket::handle);
        register(id, DecorativeWispPacket.class, DecorativeWispPacket::deserialise, DecorativeWispPacket::handle);
    }

    private static <T extends IMessage> int register(int id, Class<T> type,
                                                    Function<PacketByteBuf, T> decoder,
                                                    Consumer<T> clientHandler) {
        OWNED_MESSAGES.add(type);
        CHANNEL.registerMessage(id, type,
                (message, buf) -> buf.writeBytes(message.toBuf()),
                decoder,
                (message, supplier) -> {
                    NetworkEvent.Context context = supplier.get();
                    context.enqueueWork(() -> clientHandler.accept(message));
                    context.setPacketHandled(true);
                });
        return id + 1;
    }

    /** Whether this message type belongs to Oneironaut and therefore travels on our own channel. */
    public static boolean owns(IMessage message) {
        return OWNED_MESSAGES.contains(message.getClass());
    }

    public static void sendToPlayer(ServerPlayerEntity player, IMessage message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendNear(Vec3d pos, double radius, ServerWorld world, IMessage message) {
        CHANNEL.send(PacketDistributor.NEAR.with(
                () -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, radius, world.getRegistryKey())), message);
    }
}
