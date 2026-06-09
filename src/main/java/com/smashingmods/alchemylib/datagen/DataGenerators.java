package com.smashingmods.alchemylib.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

public class DataGenerators {

    public static void gatherData(GatherDataEvent.Client pEvent) {
        pEvent.createProvider(LocalizationGenerator::new);
    }
}
