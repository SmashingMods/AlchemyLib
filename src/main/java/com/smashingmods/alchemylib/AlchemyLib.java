package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.common.network.PacketHandler;
import com.smashingmods.alchemylib.datagen.DataGenerators;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;


/**
 * AlchemyLib is a library mod for ChemLib addon mods such as Alchemistry
 * and Techemistry. Anyone can use AlchemyLib to help bootstrap their mod
 * development. Everything you need is in the api package.
 */
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
