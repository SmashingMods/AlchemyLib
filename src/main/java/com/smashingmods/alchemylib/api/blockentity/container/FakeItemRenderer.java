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
        // Translate into the item's GUI depth band (matching vanilla's GUI item z of 150) before drawing the ghost
        // overlay. RenderType.guiGhostRecipeOverlay() uses GREATER_DEPTH_TEST without depth-write, so the gray quad must
        // sit at the item's depth to tint it; left at z 0 it bleeds through later, higher-z passes such as tooltips
        // (drawn at z 400). This mirrors OverlayRecipeComponent.OverlayRecipeButton, which pushes z 150 for the same draw.
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate(0.0F, 0.0F, 150.0F);

        pGuiGraphics.renderFakeItem(pItemStack, pX, pY);
        if (pSemiTransparent) {
            pGuiGraphics.fill(RenderType.guiGhostRecipeOverlay(), pX, pY, pX + 16, pY + 16, 0x88888888);
        }

        if (pDrawItemDecorations) {
            pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, pItemStack, pX, pY);
        }

        pGuiGraphics.pose().popPose();
    }
}
