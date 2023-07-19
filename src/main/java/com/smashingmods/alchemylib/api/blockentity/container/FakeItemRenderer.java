package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

@SuppressWarnings("unused")
public class FakeItemRenderer {

    public static void renderFakeItem(GuiGraphics pGuiGraphics, ItemStack pItemStack, int pX, int pY, float pOpacity) {
        renderFakeItem(pGuiGraphics, pItemStack, pX, pY, pOpacity, false);
    }

    public static void renderFakeItem(GuiGraphics pGuiGraphics, ItemStack pItemStack, int pX, int pY, boolean pDrawItemDecorations) {
        renderFakeItem(pGuiGraphics, pItemStack, pX, pY, 1f, pDrawItemDecorations);
    }

    public static void renderFakeItem(GuiGraphics pGuiGraphics, ItemStack pItemStack, int pX, int pY, float pOpacity, boolean pDrawItemDecorations) {
        if (pOpacity != 1f) {
            RenderSystem.setShaderColor(1f, 1f, 1f, pOpacity);
        }

        pGuiGraphics.renderFakeItem(pItemStack, pX, pY);

        if (pOpacity != 1f) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }

        if (pDrawItemDecorations) {
            pGuiGraphics.renderItemDecorations(Minecraft.getInstance().font, pItemStack, pX, pY);
        }
    }
}
