package com.smashingmods.alchemylib.api.blockentity.container.data;

import net.minecraft.network.chat.Component;

/**
 * This interface can be implemented to define a single data point for displaying
 * on AlchemyLib screens.
 *
 * @see AbstractDisplayData
 */
@SuppressWarnings("unused")
public interface DisplayData {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getValue();

    int getMaxValue();


    /**
     * @return {@link Component} to be rendered to a screen, typically in a tooltip.
     *
     * @see AbstractDisplayData#toTextComponent()
     */
    Component toTextComponent();
}
