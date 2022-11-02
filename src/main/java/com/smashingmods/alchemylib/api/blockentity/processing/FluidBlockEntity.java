package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;

@SuppressWarnings("unused")
public interface FluidBlockEntity {

    FluidStorageHandler initializeFluidStorage();

    FluidStorageHandler getFluidStorage();
}
