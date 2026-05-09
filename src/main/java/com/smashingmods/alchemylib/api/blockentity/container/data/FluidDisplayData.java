package com.smashingmods.alchemylib.api.blockentity.container.data;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.fluids.FluidStack;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Display data backed by an {@link AbstractFluidBlockEntity}'s fluid storage.
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
        return blockEntity.getFluidStorage();
    }

    @Override
    public String toString() {
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
        FluidStack fluidStack = getFluidHandler().getFluidStack();

        boolean emptyFluid = fluidStack.isEmpty();

        String fluidName = emptyFluid ? "" : String.format(" %s", I18n.get(fluidStack.getDescriptionId()).toLowerCase());
        String stored = numberFormat.format(getValue());
        String capacity = numberFormat.format(getMaxValue());
        return String.format("%s/%s mb%s", stored, capacity, fluidName);
    }
}
