package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.network.AbstractPacketHandler;
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
    public PacketHandler register() {
        registerMessage(ToggleLockButtonPacket.class, ToggleLockButtonPacket::new);
        registerMessage(TogglePauseButtonPacket.class, TogglePauseButtonPacket::new);
        registerMessage(SearchPacket.class, SearchPacket::new);
        registerMessage(BlockEntityPacket.class, BlockEntityPacket::new);
        return this;
    }
}
