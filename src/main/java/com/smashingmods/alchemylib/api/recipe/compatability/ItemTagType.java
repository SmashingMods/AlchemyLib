package com.smashingmods.alchemylib.api.recipe.compatability;

import net.minecraft.util.StringRepresentable;

@SuppressWarnings("unused")
public enum ItemTagType implements StringRepresentable {
    INGOTS("ingots"),
    DUSTS("dusts"),
    PLATES("plates"),
    NUGGETS("nuggets"),
    STORAGE_BLOCKS("storage_blocks");

    private final String type;

    ItemTagType(String pType) {
        this.type = pType;
    }

    @Override
    public String getSerializedName() {
        return type;
    }
}
