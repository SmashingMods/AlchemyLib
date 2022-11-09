package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.LinkedList;

/**
 * This interface defines the signature of lifecycle methods for a machine that processes a recipe.
 *
 * @see AbstractProcessingBlockEntity
 */
@SuppressWarnings("unused")
public interface ProcessingBlockEntity {

    /**
     * The tick method to be called every game tick that must be setup on an EntityBlock.
     *
     * @see net.minecraft.world.level.block.EntityBlock#getTicker(Level, BlockState, BlockEntityType) EntityBlock#getTicker(Level, BlockState, BlockEntityType)
     */
    void tick();

    /**
     *  This method should be called in the tick method of a machine to set the recipe based on the current state of the
     *  machine. Make sure to check if the recipe is locked before updating it.
     *
     * @see #isRecipeLocked()
     * @see AbstractProcessingBlockEntity#tick()
     */
    void updateRecipe();

    /**
     * Use this method to test whether a recipe can be processed. For example, your recipe requires an input of 2 minecraft:stone
     * but your input handler has 1: the recipe is valid, but it can't process because there isn't enough. Several other methods
     * rely on this method to provide the state. Depending on where this is implemented (in a descendant for example), the other
     * methods {@link #getCanProcess()} and {@link #setCanProcess(boolean)} are necessary.
     *
     * @return boolean
     */
    boolean canProcessRecipe();

    /**
     * The current state of the block entity.
     * @return {@link #canProcessRecipe()}
     */
    boolean getCanProcess();

    /**
     * @param pCanProcess {@link #canProcessRecipe()}
     */
    void setCanProcess(boolean pCanProcess);

    /**
     * Call this method when progress has reached max progress. The method body should handle decrementing ItemStacks or
     * other ingredients from the machine's storage and incrementing an output. Make sure to check if processing is paused
     * before processing.
     *
     * @see #isProcessingPaused()
     * @see AbstractProcessingBlockEntity#tick()
     */
    void processRecipe();

    /**
     * @param pRecipe The {@link AbstractProcessingRecipe} to set for the machine. Can be set to null.
     * @param <R> {@link AbstractProcessingRecipe}
     */
    <R extends AbstractProcessingRecipe> void setRecipe(@Nullable R pRecipe);

    /**
     * @param <R> {@link AbstractProcessingRecipe} - All AlchemyLib machines must use this recipe object.
     * @return The machine's current recipe. Can be null if the machine doesn't yet have a recipe. Make sure to check
     * if the recipe is null before attempting to access it.
     */
    @Nullable
    <R extends AbstractProcessingRecipe> R getRecipe();

    /**
     * @param <R> AbstractProcessingRecipe
     * @return LinkedList of {@link AbstractProcessingRecipe} held as a reference. Should return values from a recipe registry
     * with cached data to not harm performance. This list is necessary for searching over recipes or iterating for other
     * functionality.
     */
    <R extends AbstractProcessingRecipe> LinkedList<R> getAllRecipes();

    /**
     * @return Integer value of the current progress of the machine.
     */
    int getProgress();

    /**
     * Set the exact progress of the machine. This is typically used for loading data from persistent storage.
     */
    void setProgress(int pProgress);

    /**
     * @return Integer value of the machine implementations max progress.
     */
    int getMaxProgress();

    /**
     * This should be set when creating the machine object.
     */
    void setMaxProgress(int pMaxProgress);

    /**
     * Implement to increment the progress of the machine. This should be called in the machine's tick method to increase
     * progress over time.
     */
    void incrementProgress();

    /**
     * @return Boolean if the recipe is set to locked.
     *
     * @see AbstractProcessingBlockEntity#tick()
     */
    boolean isRecipeLocked();

    /**
     * @param pRecipeLocked Boolean to lock the recipe so that it can't be changed. Check {@link #isRecipeLocked()} when attempting
     *                      to update the recipe.
     */
    void setRecipeLocked(boolean pRecipeLocked);

    /**
     * @return Boolean if processing is paused.
     *
     * @see AbstractProcessingBlockEntity#tick()
     */
    boolean isProcessingPaused();

    /**
     * @param pPaused Boolean to pause the processing of the machine. Check {@link #isProcessingPaused()} when attempting to process.
     */
    void setPaused(boolean pPaused);
}
