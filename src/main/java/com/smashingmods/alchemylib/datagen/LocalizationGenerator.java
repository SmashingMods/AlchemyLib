package com.smashingmods.alchemylib.datagen;

import com.smashingmods.alchemylib.AlchemyLib;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

public class LocalizationGenerator extends LanguageProvider {

    public LocalizationGenerator(DataGenerator pGenerator, String pLocale) {
        super(pGenerator, AlchemyLib.MODID, pLocale);
    }

    @Override
    protected void addTranslations() {
        add("alchemylib.container.pause", "Pause");
        add("alchemylib.container.resume", "Resume");
        add("alchemylib.container.unlock_recipe", "Unlock recipe");
        add("alchemylib.container.lock_recipe", "Lock recipe");
        add("alchemylib.container.open_recipe_select", "Open Recipe Selection");
        add("alchemylib.container.close_recipe_select", "Close Recipe Selection");
        add("alchemylib.container.show_recipes", "Show Recipes");
    }
}
