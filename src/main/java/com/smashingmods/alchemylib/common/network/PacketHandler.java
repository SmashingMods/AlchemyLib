package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(PayloadRegistrar registrar) {
        registrar.playToServer(ToggleLockButtonPacket.TYPE, ToggleLockButtonPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(TogglePauseButtonPacket.TYPE, TogglePauseButtonPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToServer(SearchPacket.TYPE, SearchPacket.STREAM_CODEC, AlchemyPacket::handle);
        registrar.playToClient(BlockEntityPacket.TYPE, BlockEntityPacket.STREAM_CODEC, AlchemyPacket::handle);
    }
}
