package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.common.network.PacketHandler;
import com.smashingmods.alchemylib.datagen.DataGenerators;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;


/**
 * AlchemyLib is a library mod for ChemLib addon mods such as Alchemistry
 * and Techemistry. Anyone can use AlchemyLib to help bootstrap their mod
 * development. Everything you need is in the api package.
 */
@Mod(AlchemyLib.MODID)
public class AlchemyLib {

    public static final String MODID = "alchemylib";
    public static AlchemyLib instance;
    private final PacketHandler packetHandler = new PacketHandler();

    public AlchemyLib(IEventBus modEventBus) {
        instance = this;
        modEventBus.addListener(packetHandler::register);
        modEventBus.addListener(DataGenerators::gatherData);
    }

    public static PacketHandler getPacketHandler() {
        return instance.packetHandler;
    }
}
