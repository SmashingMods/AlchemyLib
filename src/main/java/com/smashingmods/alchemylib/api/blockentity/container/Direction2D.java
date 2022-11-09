package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * This simple enum defines the four directions on a screen for the purpose of informing
 * rendering methods which direction to render.
 *
 * @see AbstractProcessingScreen#directionalBlit(PoseStack, int, int, int, int, int, int, int, int, Direction2D)
 */
@SuppressWarnings("JavadocReference")
public enum Direction2D {
    LEFT, UP, RIGHT, DOWN
}
