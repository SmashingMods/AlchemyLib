package com.smashingmods.alchemylib.common.blockentity.processing;

import com.smashingmods.alchemylib.common.storage.EnergyStorageHandler;

public interface EnergyBlockEntity {

    EnergyStorageHandler initializeEnergyStorage();

    EnergyStorageHandler getEnergyHandler();
}
