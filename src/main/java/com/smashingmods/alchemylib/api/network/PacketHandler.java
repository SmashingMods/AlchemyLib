package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.AlchemyLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler extends AbstractPacketHandler {

    private final SimpleChannel simpleChannel;

    public PacketHandler() {
        this.simpleChannel = createChannel(new ResourceLocation(String.format("%s:main", AlchemyLib.MODID)), "1.0.0");
    }

    @Override
    protected SimpleChannel getChannel() {
        return simpleChannel;
    }

    @Override
    public void register() {
        registerClientToServer(ToggleLockButtonPacket.class, ToggleLockButtonPacket::decode);
        registerClientToServer(TogglePauseButtonPacket.class, TogglePauseButtonPacket::decode);
        registerClientToServer(SearchPacket.class, SearchPacket::decode);
        registerServerToClient(BlockEntityPacket.class, BlockEntityPacket::decode);
    }
}
