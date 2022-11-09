package com.smashingmods.alchemylib.api.blockentity.processing;

/**
 * Implement this method for a machine that uses a separate machine and {@link net.minecraft.client.gui.components.EditBox EditBox} to search over the recipes of the machine.
 *
 * @see AbstractSearchableBlockEntity
 * @see com.smashingmods.alchemylib.client.button.RecipeSelectorButton RecipeSelectorButton
 */
@SuppressWarnings("unused")
public interface SearchableBlockEntity {

    /**
     * Set this boolean based on if a separate screen being used for searching is open or closed.
     *
     * @see com.smashingmods.alchemylib.client.button.RecipeSelectorButton RecipeSelectorButton
     */
    void setRecipeSelectorOpen(boolean pOpen);

    boolean isRecipeSelectorOpen();

    /**
     * @return {@link net.minecraft.client.gui.components.EditBox EditBox} text used to search.
     */
    String getSearchText();

    /**
     * @param pText Text to set for the block entity's {@link net.minecraft.client.gui.components.EditBox EditBox}.
     */
    void setSearchText(String pText);
}
