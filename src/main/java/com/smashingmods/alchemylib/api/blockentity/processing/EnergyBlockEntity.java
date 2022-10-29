package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;

public interface EnergyBlockEntity {

    EnergyStorageHandler initializeEnergyStorage();

    EnergyStorageHandler getEnergyHandler();
}
