package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;

@SuppressWarnings("unused")
public interface FluidBlockEntity {

    FluidStorageHandler initializeFluidStorage();

    FluidStorageHandler getFluidStorage();

    ProcessingSlotHandler initializeSlotHandler();

    ProcessingSlotHandler getSlotHandler();
}
