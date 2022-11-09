package com.smashingmods.alchemylib.api.blockentity.container.data;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Extends {@link AbstractDisplayData} by passing an {@link AbstractProcessingBlockEntity} into the constructor.
 * The block entity is used as a reference to get the energy stored and max energy stored values and return them
 * in {@link #getValue()} and {@link #getMaxValue()} respectively.
 */
@SuppressWarnings("unused")
public class EnergyDisplayData extends AbstractDisplayData {

    private final AbstractProcessingBlockEntity blockEntity;

    public EnergyDisplayData(AbstractProcessingBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
    }

    @Override
    public int getValue() {
        return blockEntity.getEnergyHandler().getEnergyStored();
    }

    @Override
    public int getMaxValue() {
        return blockEntity.getEnergyHandler().getMaxEnergyStored();
    }

    @Override
    public String toString() {
        NumberFormat numFormat = NumberFormat.getInstance(Locale.US);
        String stored = numFormat.format(getValue());
        String capacity = numFormat.format(getMaxValue());
        return stored + "/" + capacity + " FE";
    }
}
