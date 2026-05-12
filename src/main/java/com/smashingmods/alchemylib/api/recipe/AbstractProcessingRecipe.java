package com.smashingmods.alchemylib.api.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * This abstract implementation of {@link ProcessingRecipe} implements default methods that should
 * be ignored by extending classes.
 */
public abstract class AbstractProcessingRecipe implements ProcessingRecipe, Comparable<AbstractProcessingRecipe> {
    @Nullable
    private ResourceLocation id;
    private final String group;

    public AbstractProcessingRecipe(String pGroup) {
        this(null, pGroup);
    }

    public AbstractProcessingRecipe(@Nullable ResourceLocation pId, String pGroup) {
        this.id = pId;
        this.group = pGroup;
    }

    @Nullable
    public ResourceLocation getId() {
        return id;
    }

    public void setId(@Nullable ResourceLocation pId) {
        this.id = pId;
    }

    @Override
    public String getGroup() {
        return group;
    }

    /**
     * This method must be overridden by the implementing class, but it's only used for
     * the base game. This class set the return to false. Override this in an implementing
     * class if another return is necessary.
     */
    @Override
    public boolean matches(RecipeInput recipeInput, Level level) {
        return false;
    }

    /**
     * This method is for returning the output item of a crafting recipe to a container / inventory. AlchemyLib
     * crafting ignores this in favor of handling this logic within block entities.
     */
    @Override
    public ItemStack assemble(RecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    /**
     * Not all implementations of this class will output an ItemStack. The default is to return
     * an empty ItemStack. If your implementing class does return an ItemStack, override this.
     */
    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    /**
     * This method is for 2x2 and 3x3 Minecraft crafting tables. Recipes extending this abstract class are designed
     * for custom crafting methods. Therefore, this simply returns false in all cases.
     */
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public boolean equals(Object pOther) {
        return pOther instanceof AbstractProcessingRecipe recipe && compareTo(recipe) == 0;
    }
}
