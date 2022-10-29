package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.api.network.PacketHandler;
import com.smashingmods.alchemylib.datagen.DataGenerators;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlchemyLib.MODID)
public class AlchemyLib {

    public static final String MODID = "alchemylib";
    public static AlchemyLib instance;
    private final PacketHandler packetHandler;

    public AlchemyLib() {
        instance = this;
        packetHandler = new PacketHandler();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetupEvent);
        modEventBus.addListener(DataGenerators::gatherData);
    }

    public static PacketHandler getPacketHandler() {
        return instance.packetHandler;
    }

    public void commonSetupEvent(final FMLCommonSetupEvent pEvent) {
        packetHandler.register();
    }
}
