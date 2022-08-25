package com.smashingmods.alchemylib.common.blockentity.container;

import net.minecraft.network.chat.Component;

public interface DisplayData {
    int getX();

    int getY();

    int getWidth();

    int getHeight();

    int getValue();

    int getMaxValue();

    Component toTextComponent();
}
