package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = AlchemyLib.MODID)
public class PacketHandler {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN);
        registrar.playToServer(ToggleLockButtonPacket.TYPE, ToggleLockButtonPacket.STREAM_CODEC, ToggleLockButtonPacket::handle);
        registrar.playToServer(TogglePauseButtonPacket.TYPE, TogglePauseButtonPacket.STREAM_CODEC, TogglePauseButtonPacket::handle);
        registrar.playToServer(SearchPacket.TYPE, SearchPacket.STREAM_CODEC, SearchPacket::handle);
        registrar.playToClient(BlockEntityPacket.TYPE, BlockEntityPacket.STREAM_CODEC, BlockEntityPacket::handle);
    }
}
