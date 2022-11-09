package com.smashingmods.alchemylib.api.recipe.compatability;

import net.minecraft.util.StringRepresentable;

/**
 * This enum defines a set of strings representing the metal types in the Thermal mod series.
 * The values can be used for mod compatibility when generating recipes.
 */
@SuppressWarnings("unused")
public enum ThermalMetalType implements StringRepresentable {
    BRONZE("bronze"),
    INVAR("invar"),
    ELECTRUM("electrum"),
    CONSTANTAN("constantan"),
    SIGNALUM("signalum"),
    LUMIUM("lumium"),
    ENDERIUM("enderium");

    private final String type;

    ThermalMetalType(String pType) {
        this.type = pType;
    }

    @Override
    public String getSerializedName() {
        return type;
    }
}