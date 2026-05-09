package com.smashingmods.alchemylib.api.storage;

import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * Wrapper around {@link EnergyStorage} that adds a hook for energy-changed events.
 */
@SuppressWarnings("unused")
public class EnergyStorageHandler extends EnergyStorage {

    public EnergyStorageHandler(int capacity) {
        super(capacity);
    }

    @SuppressWarnings("EmptyMethod")
    protected void onEnergyChanged() {}

    public void setEnergy(int pEnergy) {
        this.energy = Math.max(Math.min(pEnergy, capacity), 0);
        onEnergyChanged();
    }

    public void addEnergy(int pEnergy) {
        this.energy = Math.min(energy + pEnergy, capacity);
        onEnergyChanged();
    }

    public void consumeEnergy(int pEnergy) {
        this.energy = Math.max(energy - pEnergy, 0);
        onEnergyChanged();
    }
}
