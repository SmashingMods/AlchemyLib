package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import com.smashingmods.alchemylib.common.network.PacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Implement AlchemyPacket to create your own packets to send across the network.
 *
 * <p>AlchemyPacket extends {@link CustomPacketPayload}, so every packet must supply a unique
 * {@link CustomPacketPayload#type() type} backed by a {@code static final}
 * {@link CustomPacketPayload.Type Type} field.</p>
 *
 * <p>Serialization is handled by a {@code static final}
 * {@link StreamCodec StreamCodec&lt;RegistryFriendlyByteBuf, MSG&gt;} field rather than a {@code write}
 * method. The codec is passed when the packet is registered and is used to encode the packet on the
 * sending side and decode it on the receiving side.</p>
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
     * of the receiving side via {@link IPayloadContext#enqueueWork(Runnable)} before it runs.</p>
     *
     * @param pContext {@link IPayloadContext}
     *
     * @see BlockEntityPacket#handle(IPayloadContext)
     */
    void handle(IPayloadContext pContext);
}
