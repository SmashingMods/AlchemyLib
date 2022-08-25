package com.smashingmods.alchemylib.common.blockentity.processing;

import com.smashingmods.alchemylib.common.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.common.storage.ProcessingSlotHandler;

public interface FluidBlockEntity {

    FluidStorageHandler initializeFluidStorage();

    FluidStorageHandler getFluidStorage();

    ProcessingSlotHandler initializeSlotHandler();

    ProcessingSlotHandler getSlotHandler();
}
