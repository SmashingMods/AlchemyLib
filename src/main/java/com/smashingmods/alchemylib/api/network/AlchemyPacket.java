package com.smashingmods.alchemylib.api.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface AlchemyPacket {

    void encode(FriendlyByteBuf pBuffer);

    void handle(NetworkEvent.Context pContext);

    static <MSG extends AlchemyPacket> void handle(final MSG pMessage, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> pMessage.handle(pContext.get()));
        pContext.get().setPacketHandled(true);
    }
}
