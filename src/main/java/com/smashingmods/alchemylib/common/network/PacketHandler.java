package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(RegisterPayloadHandlersEvent pEvent) {
        registrar(pEvent, AlchemyLib.MODID);
        registerServerBound(ToggleLockButtonPacket.TYPE, ToggleLockButtonPacket.STREAM_CODEC);
        registerServerBound(TogglePauseButtonPacket.TYPE, TogglePauseButtonPacket.STREAM_CODEC);
        registerServerBound(SearchPacket.TYPE, SearchPacket.STREAM_CODEC);
        registerClientBound(BlockEntityPacket.TYPE, BlockEntityPacket.STREAM_CODEC);
    }
}
