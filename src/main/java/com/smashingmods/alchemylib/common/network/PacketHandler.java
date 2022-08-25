package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static int PACKET_ID = 0;
    private static final String PROTOCOL_VERSION = "1.0";

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AlchemyLib.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {

        INSTANCE.messageBuilder(ToggleLockButtonPacket.class, PACKET_ID++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ToggleLockButtonPacket::new)
                .encoder(ToggleLockButtonPacket::encode)
                .consumer(ToggleLockButtonPacket::handle)
                .add();

        INSTANCE.messageBuilder(TogglePauseButtonPacket.class, PACKET_ID++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(TogglePauseButtonPacket::new)
                .encoder(TogglePauseButtonPacket::encode)
                .consumer(TogglePauseButtonPacket::handle)
                .add();
    }
}
