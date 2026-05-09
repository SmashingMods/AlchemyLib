package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.common.network.PacketHandler;
import com.smashingmods.alchemylib.datagen.DataGenerators;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

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
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(DataGenerators::gatherData);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        packetHandler.register(event.registrar(MODID));
    }

    public static PacketHandler getPacketHandler() {
        return instance.packetHandler;
    }
}
