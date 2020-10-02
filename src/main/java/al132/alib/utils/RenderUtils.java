package al132.alib.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.lwjgl.opengl.GL11;

//From endercore, Creative Commons license, https://github.com/SleepyTrousers/EnderCore
public class RenderUtils {

    public static ResourceLocation BLOCK_TEX = AtlasTexture.LOCATION_BLOCKS_TEXTURE;

    public static TextureManager engine() {
        return Minecraft.getInstance().getTextureManager();
    }

    public static void bindBlockTexture() {
        engine().bindTexture(BLOCK_TEX);
    }

    public void bindTexture(String string) {
        engine().bindTexture(new ResourceLocation(string));
    }

    public void bindTexture(ResourceLocation tex) {
        engine().bindTexture(tex);
    }


    public static TextureAtlasSprite getStillTexture(FluidStack fluidstack) {
        Fluid fluid = fluidstack.getFluid();
        return getStillTexture(fluidstack.getFluid());
    }

    public static TextureAtlasSprite getStillTexture(Fluid fluid) {
        ResourceLocation iconKey = fluid.getAttributes().getStillTexture();
        if (iconKey == null) return null;
        return Minecraft.getInstance().getAtlasSpriteGetter(AtlasTexture.LOCATION_BLOCKS_TEXTURE).apply(iconKey);
    }

    public static void renderGuiTank(FluidTank tank, double x, double y, double zLevel, double width, double height) {
        renderGuiTank(tank.getFluid(), tank.getCapacity(), tank.getFluidAmount(), x, y, zLevel, width, height);
    }

    public static void renderGuiTank(FluidStack fluid, int capacity, int amount, double x, double y, double zLevel, double width, double height) {
        if (fluid == null || fluid.getFluid() == null || fluid.getAmount() <= 0) return;

        TextureAtlasSprite icon = null;
        if (getStillTexture(fluid) != null) icon = getStillTexture(fluid);

        int renderAmount = (int) Math.max(Math.min(height, amount * height / capacity), 1.0);
        int posY = (int) (y + height - renderAmount);

        RenderUtils.bindBlockTexture();
        int color = fluid.getFluid().getAttributes().getColor(fluid);
        GL11.glColor3ub((byte) (color >> 16 & 0xFF), (byte) (color >> 8 & 0xFF), (byte) (color & 0xFF));

        //GlStateManager.enableBlend();
        RenderSystem.enableBlend();
        int i = 0;
        while (i < width) {
            int j = 0;
            while (j < renderAmount) {
                int drawWidth = (int) Math.min(width - i, 16.0);
                int drawHeight = Math.min(renderAmount - j, 16);

                int drawX = (int) (x + i);
                int drawY = (int) posY + j;

                float minU = icon.getMinU();
                float maxU = icon.getMaxU();
                float minV = icon.getMinV();
                float maxV = icon.getMaxV();

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buf = tessellator.getBuffer();
                buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                buf.pos((double) drawX, (double) (drawY + drawHeight), 0.0).tex(minU, minV + (maxV - minV) * drawHeight / 16f).endVertex();
                buf.pos((double) (drawX + drawWidth), (double) (drawY + drawHeight), 0.0).tex(minU + (maxU - minU) * drawWidth / 16f, minV + (maxV - minV) * drawHeight / 16f).endVertex();
                buf.pos((double) (drawX + drawWidth), (double) drawY, 0.0).tex(minU + (maxU - minU) * drawWidth / 16f, minV).endVertex();
                buf.pos((double) drawX, (double) drawY, 0.0).tex(minU, minV).endVertex();
                tessellator.draw();
                j += 16;
            }
            i += 16;
        }
        RenderSystem.disableBlend();
        //GlStateManager.disableBlend();
    }
}