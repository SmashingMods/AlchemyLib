package com.smashingmods.alchemylib.api.storage;

import net.minecraftforge.energy.EnergyStorage;

/**
 * This class is a wrapper around {@link EnergyStorage} which adds a call to {@link EnergyStorageHandler#onEnergyChanged()}
 * in all methods for modifying the energy state of extended EnergyStorage.
 */
@SuppressWarnings("unused")
public class EnergyStorageHandler extends EnergyStorage {

    public EnergyStorageHandler(int capacity) {
        super(capacity);
    }

    /**
     * Override this method when instantiating a new EnergyStorageHandler. You can then use
     * this method as an event handler in the case that the handler changes. This method is called
     * in {@link EnergyStorageHandler#setEnergy(int)}, {@link EnergyStorageHandler#addEnergy(int)}, and {@link EnergyStorageHandler#consumeEnergy(int)}
     */
    @SuppressWarnings("EmptyMethod")
    protected void onEnergyChanged() {}

    /**
     * Sets the energy value to the parameter value so long as that value is
     * higher than 0 and less than capacity.
     */
    public void setEnergy(int pEnergy) {
        this.energy = Math.max(Math.min(pEnergy, capacity), 0);
        onEnergyChanged();
    }

    /**
     * Increases the energy value by the parameter value up to capacity.
     */
    public void addEnergy(int pEnergy) {
        this.energy = Math.min(energy + pEnergy, capacity);
        onEnergyChanged();
    }

    /**
     * Decreases the energy value by the parameter value down to 0.
     */
    public void consumeEnergy(int pEnergy) {
        this.energy = Math.max(energy - pEnergy, 0);
        onEnergyChanged();
    }
}
