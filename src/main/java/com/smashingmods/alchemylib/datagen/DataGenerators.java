package com.smashingmods.alchemylib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();
        generator.addProvider(pEvent.includeServer(), new LocalizationGenerator(generator, "en_us"));
    }
}
