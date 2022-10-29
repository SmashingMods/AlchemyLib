package com.smashingmods.alchemylib.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent pEvent) {
        DataGenerator generator = pEvent.getGenerator();

        if (pEvent.includeServer()) {
            generator.addProvider(new LocalizationGenerator(generator, "en_us"));
        }
    }
}
