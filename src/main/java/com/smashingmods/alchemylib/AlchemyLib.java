package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.api.network.PacketHandler;
import com.smashingmods.alchemylib.datagen.DataGenerators;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AlchemyLib.MODID)
public class AlchemyLib {

    public static final String MODID = "alchemylib";
    public static AlchemyLib instance;
    private final PacketHandler packetHandler = new PacketHandler().register();

    public AlchemyLib() {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(DataGenerators::gatherData);
    }

    public static PacketHandler getPacketHandler() {
        return instance.packetHandler;
    }
}
