package com.smashingmods.alchemylib.datagen;

import com.smashingmods.alchemylib.AlchemyLib;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AlchemyLib.MODID)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();

        generator.addProvider(pEvent.includeServer(), new LocalizationGenerator(generator.getPackOutput()));
    }
}
