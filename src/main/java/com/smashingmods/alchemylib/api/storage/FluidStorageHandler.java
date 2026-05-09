package com.smashingmods.alchemylib.api.storage;

import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/**
 * Wrapper around {@link FluidTank} with helpers for adjusting amounts.
 */
@SuppressWarnings("unused")
public class FluidStorageHandler extends FluidTank {

    public FluidStorageHandler(int pCapacity, FluidStack pFluidStack) {
        super(pCapacity);
        fill(pFluidStack, FluidAction.EXECUTE);
    }

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

    public void setAmount(int pValue) {
        fluid.setAmount(Math.max(Math.min(pValue, capacity), 0));
    }

    public void fillAmount(int pValue) {
        fluid.setAmount(Math.min(getFluidAmount() + pValue, capacity));
    }

    public void drainAmount(int pValue) {
        fluid.setAmount(Math.max(getFluidAmount() - pValue, 0));
    }

    public FluidStack getFluidStack() {
        return fluid;
    }
}
