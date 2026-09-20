package net.beholderface.oneironaut.fabric

import net.beholderface.oneironaut.network.*
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import java.util.function.Consumer
import java.util.function.Function

object FabricPacketHandler {
    fun initClientBound(){
        ClientPlayNetworking.registerGlobalReceiver(ParticleBurstPacket.ID, makeClientBoundHandler(ParticleBurstPacket::deserialise, ParticleBurstPacket::handle))
        ClientPlayNetworking.registerGlobalReceiver(FireballUpdatePacket.ID, makeClientBoundHandler(FireballUpdatePacket::deserialise, FireballUpdatePacket::handle))
        ClientPlayNetworking.registerGlobalReceiver(ItemUpdatePacket.ID, makeClientBoundHandler(ItemUpdatePacket::deserialise, ItemUpdatePacket::handle))
        ClientPlayNetworking.registerGlobalReceiver(UnBrainsweepPacket.ID, makeClientBoundHandler(UnBrainsweepPacket::deserialise, UnBrainsweepPacket::handle))
        ClientPlayNetworking.registerGlobalReceiver(SpoopyScreamPacket.ID, makeClientBoundHandler(SpoopyScreamPacket::deserialise, SpoopyScreamPacket::handle))
        ClientPlayNetworking.registerGlobalReceiver(HoverliftAntiDesyncPacket.ID, makeClientBoundHandler(HoverliftAntiDesyncPacket::deserialise, HoverliftAntiDesyncPacket::handle))
    }

    private fun <T> makeClientBoundHandler(decoder: Function<PacketByteBuf, T>, handler: Consumer<T>): ClientPlayNetworking.PlayChannelHandler {
        return ClientPlayNetworking.PlayChannelHandler { _: MinecraftClient, _: ClientPlayNetworkHandler, buf: PacketByteBuf, _: PacketSender ->
            handler.accept(decoder.apply(buf))
        }
    }
}