package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.common.network.PacketHandler;
import com.smashingmods.alchemylib.datagen.DataGenerators;
import net.minecraft.resources.ResourceLocation;
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

    public AlchemyLib(IEventBus modEventBus) {
        instance = this;
        modEventBus.addListener(DataGenerators::gatherData);
    }

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
