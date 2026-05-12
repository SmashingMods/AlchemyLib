package com.smashingmods.alchemylib.api.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link ProcessingRecipe}'s vanilla-recipe stub methods. AlchemyLib
 * machines drive their own matching/assembly logic in their block entities, so vanilla's
 * matches/assemble/getResultItem are unused and return inert defaults here.
 *
 * <p>1.21 dropped {@code Recipe.getId()} in favor of {@link net.minecraft.world.item.crafting.RecipeHolder};
 * the {@link #getId()}/{@link #setId(ResourceLocation)} pair here is a transient lookup helper that
 * the recipe-registry cache populates at hydration time so block entities can save and reload the
 * recipe they are currently processing.
 */
public abstract class AbstractProcessingRecipe implements ProcessingRecipe, Comparable<AbstractProcessingRecipe> {

    private final String group;
    @Nullable
    private ResourceLocation recipeId;

    public AbstractProcessingRecipe(String pGroup) {
        this.group = pGroup;
    }

    @Nullable
    public ResourceLocation getId() {
        return recipeId;
    }

    public void setId(@Nullable ResourceLocation pId) {
        this.recipeId = pId;
    }

    /**
     * Null-safe comparator helper for subclass {@code compareTo(...)} implementations. Recipes are
     * not guaranteed to have an id until the recipe-registry cache hydrates them from
     * {@link net.minecraft.world.item.crafting.RecipeHolder}, so direct {@code getId().compareTo(...)}
     * can NPE during early-startup access.
     */
    public static int compareIds(@Nullable ResourceLocation pA, @Nullable ResourceLocation pB) {
        if (pA == null && pB == null) return 0;
        if (pA == null) return -1;
        if (pB == null) return 1;
        return pA.compareNamespaced(pB);
    }

    /**
     * This method must be overridden by the implementing class, but it's only used for
     * the base game. This class set the return to false. Override this in an implementing
     * class if another return is necessary.
     */
    @Override
    public String getGroup() {
        return group;
    }

    /**
     * This method is for returning the output item of a crafting recipe to a container / inventory. AlchemyLib
     * crafting ignores this in favor of handling this logic within block entities.
     */
    @Override
    public boolean matches(RecipeInput pInput, Level pLevel) {
        return false;
    }

    /**
     * Not all implementations of this class will output an ItemStack. The default is to return
     * an empty ItemStack. If your implementing class does return an ItemStack, override this.
     */
    @Override
    public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pProvider) {
        return ItemStack.EMPTY;
    }

    /**
     * This method is for 2x2 and 3x3 Minecraft crafting tables. Recipes extending this abstract class are designed
     * for custom crafting methods. Therefore, this simply returns false in all cases.
     */
    @Override
    public ItemStack getResultItem(HolderLookup.Provider pProvider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public boolean equals(Object pOther) {
        return pOther instanceof AbstractProcessingRecipe recipe && compareTo(recipe) == 0;
    }
}
