package com.smashingmods.alchemylib.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

//From endercore, Creative Commons license, https://github.com/SleepyTrousers/EnderCore
public class RenderUtils {

    // public static TextureManager engine() {
    //     return Minecraft.getInstance().getTextureManager();
    // }

    public static void bindBlockTexture() {
        Minecraft.getInstance().textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);
    }

    public static TextureAtlasSprite getStillTexture(FluidStack fluidstack) {
        return getStillTexture(fluidstack.getFluid());
    }

    public static TextureAtlasSprite getStillTexture(Fluid fluid) {
        ResourceLocation iconKey = fluid.getAttributes().getStillTexture();
        if (iconKey == null) return null;
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(iconKey);
    }

    public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height) {
        renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
    }

    public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
        if (fluid == null || fluid.getFluid() == null || fluid.getAmount() <= 0) return;

        TextureAtlasSprite icon = null;
        if (getStillTexture(fluid) != null) icon = getStillTexture(fluid);
        else return;

        int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1.0);
        int posY = (int) (y + height - renderAmount);

        //RenderUtils.bindBlockTexture();
        int color = fluid.getFluid().getAttributes().getColor(fluid);
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        setGLColorFromInt(color);//GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

        //Minecraft.getInstance().getEntityRenderDispatcher().textureManager.bindForSetup(TextureAtlas.LOCATION_BLOCKS);

        //GlStateManager.enableBlend();
        //RenderSystem.enableBlend();
        int i = 0;
        while (i < width) {
            int j = 0;
            while (j < renderAmount) {
        int drawWidth = (int) Math.min(width - i, 16.0);
        int drawHeight = Math.min(renderAmount - j, 16);

        int drawX = (int) (x + i);
        int drawY = (int) posY + j;

        float minU = icon.getU0();
        float maxU = icon.getU1();
        float minV = icon.getV0();
        float maxV = icon.getV1();

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buf = tessellator.getBuilder();
        buf.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buf.vertex((double) drawX, (double) (drawY + drawHeight), 0.0).uv(minU, minV + (maxV - minV) * drawHeight / 16f).endVertex();
        buf.vertex((double) (drawX + drawWidth), (double) (drawY + drawHeight), 0.0).uv(minU + (maxU - minU) * drawWidth / 16f, minV + (maxV - minV) * drawHeight / 16f).endVertex();
        buf.vertex((double) (drawX + drawWidth), (double) drawY, 0.0).uv(minU + (maxU - minU) * drawWidth / 16f, minV).endVertex();
        buf.vertex((double) drawX, (double) drawY, 0.0).uv(minU, minV).endVertex();
        tessellator.end();
                j += 16;
            }
            i += 16;
        }

        //RenderSystem.disableBlend();
        //GlStateManager.disableBlend();
    }

    //https://github.com/McJtyMods/McJtyLib/blob/1.18/src/main/java/mcjty/lib/client/RenderHelper.java
    private static void setGLColorFromInt(int color) {
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;

        RenderSystem.setShaderColor(red, green, blue, 1.0F);
    }
}