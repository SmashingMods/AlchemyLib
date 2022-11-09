package com.smashingmods.alchemylib.api.blockentity.container.data;

import com.smashingmods.alchemylib.api.blockentity.container.Direction2D;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

/**
 * Extends {@link AbstractDisplayData} by passing an {@link AbstractProcessingBlockEntity} into the constructor.
 * The block entity is used as a reference to get the current progress and max progress values and return them
 * in {@link #getValue()} and {@link #getMaxValue()} respectively.
 */
public class ProgressDisplayData extends AbstractDisplayData {

    private final AbstractProcessingBlockEntity blockEntity;
    private final Direction2D direction2D;

    public ProgressDisplayData(AbstractProcessingBlockEntity pBlockEntity, int pX, int pY, int pWidth, int pHeight, Direction2D pDirection2D) {
        super(pX, pY, pWidth, pHeight);
        this.blockEntity = pBlockEntity;
        this.direction2D = pDirection2D;
    }

    @Override
    public int getValue() {
        return blockEntity.getProgress();
    }

    @Override
    public int getMaxValue() {
        return blockEntity.getMaxProgress();
    }

    public Direction2D getDirection() {
        return direction2D;
    }

    @Override
    public MutableComponent toTextComponent() {
        return MutableComponent.create(new TranslatableContents("alchemylib.container.show_recipes"));
    }
}
