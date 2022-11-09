package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;

/**
 * This interface should be implemented by a {@link ProcessingBlockEntity} class to add energy storage.
 */
public interface EnergyBlockEntity {

    /**
     * Use this method to initialize the energy storage of a processing block entity. This allows an abstract class to
     * set an energy storage handler field to the value of this initializer which is defined in the implemented class.
     *
     * @return {@link EnergyStorageHandler}
     *
     * @see AbstractProcessingBlockEntity
     */
    EnergyStorageHandler initializeEnergyStorage();

    /**
     * @return Implemented and initialized {@link EnergyStorageHandler}
     */
    EnergyStorageHandler getEnergyHandler();
}
