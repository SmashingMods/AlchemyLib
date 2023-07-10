package com.smashingmods.alchemylib.api.blockentity.container;

import net.minecraft.client.gui.GuiGraphics;

/**
 * This simple enum defines the four directions on a screen for the purpose of informing
 * rendering methods which direction to render.
 *
 * @see AbstractProcessingScreen#directionalBlit(GuiGraphics, int, int, int, int, int, int, int, int, Direction2D)
 */
public enum Direction2D {
    LEFT, UP, RIGHT, DOWN
}
