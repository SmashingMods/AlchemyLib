package com.smashingmods.alchemylib;

import com.mojang.logging.LogUtils;
import com.smashingmods.alchemylib.common.network.PacketHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(AlchemyLib.MODID)
public class AlchemyLib {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "alchemylib";

    public AlchemyLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetupEvent);
    }

    public void commonSetupEvent(final FMLCommonSetupEvent event) {
        PacketHandler.register();
    }
}
