package com.smashingmods.alchemylib.api.blockentity.container.data;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * This interface can be implemented to define a single data point for displaying
 * on AlchemyLib screens.
 *
 * @see AbstractDisplayData
 */
public interface DisplayData {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getValue();

    int getMaxValue();


    /**
     * @return {@link MutableComponent} to be rendered to a screen, typically in a tooltip.
     *
     * @see AbstractDisplayData#toTextComponent()
     */
    Component toTextComponent();
}
