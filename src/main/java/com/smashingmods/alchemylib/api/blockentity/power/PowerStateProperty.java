package com.smashingmods.alchemylib.api.blockentity.power;

import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * {@link net.minecraft.world.level.block.state.BlockState BlockState} property wrapper for {@link PowerState} for persisting that state.
 *
 * <p>{@link EnumProperty} is final, so this holds the shared {@link #POWER_STATE} property rather than extending it.</p>
 */
@SuppressWarnings("unused")
public final class PowerStateProperty {

    public static final EnumProperty<PowerState> POWER_STATE = EnumProperty.create("power_state", PowerState.class);

    private PowerStateProperty() {
    }
}
