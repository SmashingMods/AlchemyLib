package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;

public class PacketHandler extends AbstractPacketHandler {

    @Override
    public void register(RegisterPayloadHandlerEvent pEvent) {
        registrar(pEvent, AlchemyLib.MODID);
        registerServerBound(ToggleLockButtonPacket.ID, ToggleLockButtonPacket::new);
        registerServerBound(TogglePauseButtonPacket.ID, TogglePauseButtonPacket::new);
        registerServerBound(SearchPacket.ID, SearchPacket::new);
        registerClientBound(BlockEntityPacket.ID, BlockEntityPacket::new);
    }
}
