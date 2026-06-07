package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import com.smashingmods.alchemylib.common.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

/**
 * Implement AlchemyPacket to create your own packets to send across the network.
 *
 * <p>AlchemyPacket extends {@link CustomPacketPayload}, so every packet must supply a unique
 * {@link CustomPacketPayload#id() id} and a {@link CustomPacketPayload#write(FriendlyByteBuf) write}
 * method that serializes its data to the outgoing buffer.</p>
 *
 * <p>Implementing classes also need a {@link FriendlyByteBuf} constructor. It is used as a decoder to
 * create a new packet object on the receiving side and is passed as the
 * {@link FriendlyByteBuf.Reader reader} when the packet is registered.</p>
 *
 * <p>The packet must be registered in your implementation of {@link AbstractPacketHandler#register} using
 * {@link AbstractPacketHandler#registerClientBound} or {@link AbstractPacketHandler#registerServerBound},
 * depending on which side is meant to receive it.</p>
 *
 * @see BlockEntityPacket
 * @see PacketHandler
 */
public interface AlchemyPacket extends CustomPacketPayload {

    /**
     * This method is called on the receiving end to handle the packet. Whatever your packet does,
     * this is where you do it.
     *
     * <p>The packet is decoded on the network thread, so this method is enqueued onto the main thread
     * of the receiving side via {@link PlayPayloadContext#workHandler()} before it runs.</p>
     *
     * @param pContext {@link PlayPayloadContext}
     *
     * @see BlockEntityPacket#handle(PlayPayloadContext)
     */
    void handle(PlayPayloadContext pContext);
}
