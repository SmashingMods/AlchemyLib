package com.smashingmods.alchemylib.api.blockentity.processing;

@SuppressWarnings("unused")
public interface SearchableBlockEntity {

    void setRecipeSelectorOpen(boolean pOpen);

    boolean isRecipeSelectorOpen();

    String getSearchText();

    void setSearchText(String pText);
}
