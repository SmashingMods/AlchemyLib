package com.smashingmods.alchemylib.api.storage;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

/**
 * This class is a wrapper around {@link FluidTank} that adds some helper methods.
 * for setting the fluid amount held in the tank.
 */
@SuppressWarnings("unused")
public class FluidStorageHandler extends FluidTank {

    public FluidStorageHandler(int pCapacity, FluidStack pFluidStack) {
        super(pCapacity);
        fill(pFluidStack, FluidAction.EXECUTE);
    }

    /**
     * Set the FluidStack of this tank. If the tank already had a fluid set, it will drain that fluid
     * to set the new fluid. Helper methods exist to set the FluidStack using a fluid with an optional amount.
     *
     * @param pFluidStack {@link FluidStack}
     */
    public void setFluid(FluidStack pFluidStack) {
        drain(capacity, FluidAction.EXECUTE);
        fill(pFluidStack, FluidAction.EXECUTE);
    }

    public void setFluid(Fluid pFluid, int pAmount) {
        setFluid(new FluidStack(pFluid, pAmount));
    }

    public void setFluid(Fluid pFluid) {
        setFluid(pFluid, 0);
    }

    /**
     * Sets the fluid amount to the parameter value so long as that value is
     * higher than 0 and less than capacity.
     */
    public void setAmount(int pValue) {
        fluid.setAmount(Math.max(Math.min(pValue, capacity), 0));
    }

    /**
     * Fills the fluid amount by the parameter value up to capacity.
     */
    public void fillAmount(int pValue) {
        fluid.setAmount(Math.min(getFluidAmount() + pValue, capacity));
    }

    /**
     * Drains the fluid amount by the parameter down to 0.
     */
    public void drainAmount(int pValue) {
        fluid.setAmount(Math.max(getFluidAmount() - pValue, 0));
    }

    public FluidStack getFluidStack() {
        return fluid;
    }
}
