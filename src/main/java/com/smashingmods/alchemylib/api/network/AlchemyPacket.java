package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import com.smashingmods.alchemylib.common.network.PacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implement AlchemyPacket to create your own packets to send across the network.
 *
 * <p>Implementing classes will need two constructors. The first is to create the packet elsewhere in your code.
 * The second constructor is used as a decoder to create a new packet object on the receiving side.</p>
 *
 * <p>The packet must be registered in your implementation of {@link AbstractPacketHandler#register} using
 * {@link AbstractPacketHandler#registerMessage(Class, Function)}</p>
 *
 * @see BlockEntityPacket
 * @see PacketHandler
 */
public interface AlchemyPacket {

    /**
     * Implement this method to encode your packet's data to a FriendlyByteBuf that will
     * be sent across the network.
     *
     * @param pBuffer {@link FriendlyByteBuf}
     *
     * @see BlockEntityPacket#encode(FriendlyByteBuf)
     */
    void encode(FriendlyByteBuf pBuffer);

    /**
     * This method is called on the receiving end to handle the enqueued work. Whatever your packet does,
     * this is where you do it.
     *
     * @param pContext NetworkEvent.Context
     *
     * @see BlockEntityPacket#handle(NetworkEvent.Context)
     */
    void handle(NetworkEvent.Context pContext);

    /**
     * Wrapper method for all packets implementing AlchemyPacket. This method enqueues work for {@link AlchemyPacket#handle(NetworkEvent.Context)}
     * and sets the packet as handled when done.
     *
     * @param pMessage The implementing packet.
     * @param pContext Supplier of {@link net.minecraftforge.network.NetworkEvent.Context}
     * @param <MSG> AlchemyPacket
     */
    static <MSG extends AlchemyPacket> void handle(final MSG pMessage, Supplier<NetworkEvent.Context> pContext) {
        NetworkEvent.Context context = pContext.get();
        context.enqueueWork(() -> pMessage.handle(context));
        context.setPacketHandled(true);
    }
}
