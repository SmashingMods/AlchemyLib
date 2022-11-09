package com.smashingmods.alchemylib.api.blockentity.power;

import net.minecraft.util.StringRepresentable;

/**
 * Enum representing the power state of a machine. It can be used in a machine's tick method to determine the state
 * of the machine based on other state context such as energy, multiblock configuration, or something else.
 *
 * <ul>
 *     <li>DISABLED - The machine isn't connected to powered or configured correctly. Used typically with a multiblock structure
 *     where disabled means that the multiblock structure isn't valid.</li>
 *     <li>OFF - The machine is turned off, similar to DISABLED and used in its place if a machine only has 3 power states.</li>
 *     <li>STANDBY - The machine has power and can process, however it's not currently processing anything.</li>
 *     <li>ON - The machine has power, is valid, and is currently processing something.</li>
 * </ul>
 */
public enum PowerState implements StringRepresentable {
    DISABLED("disabled"),
    OFF("off"),
    STANDBY("standby"),
    ON("on");

    private final String name;

    PowerState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
