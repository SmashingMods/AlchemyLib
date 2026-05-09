package com.smashingmods.alchemylib.api.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * Marker recipe shape for AlchemyLib processing machines. Input/output types are intentionally
 * untyped because each implementation can mix item/fluid/probability inputs and outputs; consumers
 * cast as needed.
 */
@SuppressWarnings("unused")
public interface ProcessingRecipe extends Recipe<RecipeInput> {

    /**
     * Create a copy of this recipe that can be mutated without affecting the original.
     */
    ProcessingRecipe copy();

    Object getInput();

    Object getOutput();
}
