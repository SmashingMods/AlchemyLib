package com.smashingmods.alchemylib.api.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * This abstract implementation of {@link ProcessingRecipe} implements default methods that should
 * be ignored by extending classes.
 */
public abstract class AbstractProcessingRecipe implements ProcessingRecipe, Comparable<AbstractProcessingRecipe> {

    private final ResourceLocation recipeId;
    private final String group;

    public AbstractProcessingRecipe(ResourceLocation pRecipeId, String pGroup) {
        this.recipeId = pRecipeId;
        this.group = pGroup;
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
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
    public boolean matches(Inventory pContainer, Level pLevel) {
        return false;
    }

    /**
     * This method is for returning the output item of a crafting recipe to a container / inventory. AlchemyLib
     * crafting ignores this in favor of handling this logic within block entities.
     */
    @Override
    public ItemStack assemble(Inventory pContainer, RegistryAccess pRegistryAccess) {
        return ItemStack.EMPTY;
    }

    /**
     * Not all implementations of this class will output an ItemStack. The default is to return
     * an empty ItemStack. If your implementing class does return an ItemStack, override this.
     */
    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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
}
