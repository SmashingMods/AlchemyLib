package com.smashingmods.alchemylib.api.blockentity.container;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class FakeItemRenderer {

    public static void renderFakeItem(GuiGraphics pGuiGraphics, ItemStack pItemStack, int pX, int pY) {
        renderFakeItem(pGuiGraphics, pItemStack, pX, pY, true, false);
    }

    public static void renderFakeItem(GuiGraphics pGuiGraphics, ItemStack pItemStack, int pX, int pY, boolean pDrawItemDecorations) {
        renderFakeItem(pGuiGraphics, pItemStack, pX, pY, true, pDrawItemDecorations);
    }

    public static void renderFakeItem(GuiGraphics pGuiGraphics, ItemStack pItemStack, int pX, int pY, boolean pSemiTransparent, boolean pDrawItemDecorations) {
        pGuiGraphics.renderFakeItem(pItemStack, pX, pY);
        if (pSemiTransparent) {
            pGuiGraphics.fill(RenderType.guiGhostRecipeOverlay(), pX, pY, pX + 16, pY + 16, 0x88888888);
        }

        if (pDrawItemDecorations) {
            pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, pItemStack, pX, pY);
        }
    }
}
