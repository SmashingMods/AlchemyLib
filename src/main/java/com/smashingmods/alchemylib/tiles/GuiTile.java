package com.smashingmods.alchemylib.tiles;

public interface GuiTile {
    default int getWidth() {
        return 175;
    }

    default int getHeight() {
        return 183;
    }
}
