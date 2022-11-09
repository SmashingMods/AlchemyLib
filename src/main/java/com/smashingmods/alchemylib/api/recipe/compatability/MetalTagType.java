package com.smashingmods.alchemylib.api.recipe.compatability;

import net.minecraft.util.StringRepresentable;

/**
 * This enum defines a set of strings representing the standard metal item tags
 * used for datagen.
 */
@SuppressWarnings("unused")
public enum MetalTagType implements StringRepresentable {
    INGOTS("ingots"),
    DUSTS("dusts"),
    PLATES("plates"),
    NUGGETS("nuggets"),
    STORAGE_BLOCKS("storage_blocks");

    private final String type;

    MetalTagType(String pType) {
        this.type = pType;
    }

    @Override
    public String getSerializedName() {
        return type;
    }
}
