package com.smashingmods.alchemylib.api.blockentity.container.data;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Extends {@link AbstractDisplayData} by passing an {@link AbstractProcessingBlockEntity} into the constructor.
 * The block entity is used as a reference to get the fluid amount stored and fluid capacity values and return them
 * in {@link #getValue()} and {@link #getMaxValue()} respectively.
 */
public class FluidDisplayData extends AbstractDisplayData {

    private final AbstractFluidBlockEntity blockEntity;

    public FluidDisplayData(AbstractFluidBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
    }

    @Override
    public int getValue() {
        return blockEntity.getFluidStorage().getFluidAmount();
    }

    @Override
    public int getMaxValue() {
        return blockEntity.getFluidStorage().getCapacity();
    }

    public FluidStorageHandler getFluidHandler() {
        return (FluidStorageHandler) blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElseGet(() -> new FluidStorageHandler(0, FluidStack.EMPTY));
    }

    @Override
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        FluidStack fluidStack = getFluidHandler().getFluidStack();

        boolean emptyFluid = fluidStack.isFluidEqual(FluidStack.EMPTY);

        String fluidName = emptyFluid ? "" : String.format(" %s", I18n.get(fluidStack.getTranslationKey()).toLowerCase());
        String stored = numberFormat.format(getValue());
        String capacity = numberFormat.format(getMaxValue());
        return String.format("%s/%s mb%s", stored, capacity, fluidName);
    }
}
