package com.smashingmods.alchemylib.datagen;

import net.minecraft.data.DataGenerator;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber
public class DataGenerators {

    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();

        generator.addProvider(pEvent.includeServer(), new LocalizationGenerator(generator.getPackOutput()));
    }
}
