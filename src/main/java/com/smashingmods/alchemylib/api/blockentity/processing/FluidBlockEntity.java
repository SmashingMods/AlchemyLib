package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;

/**
 * This interface should be implemented by a {@link ProcessingBlockEntity} class to add fluid storage.
 *
 * @see AbstractFluidBlockEntity
 */
@SuppressWarnings("unused")
public interface FluidBlockEntity {

    /**
     * Use this method to initialize the fluid storage of a processing block entity. This allows an abstract class to
     * set a fluid storage handler field to the value of this initializer which is defined in the implemented class.
     *
     * @return {@link FluidStorageHandler}
     *
     * @see AbstractFluidBlockEntity
     */
    FluidStorageHandler initializeFluidStorage();

    /**
     * @return Implemented and initialized {@link FluidStorageHandler}.
     */
    FluidStorageHandler getFluidStorage();
}
