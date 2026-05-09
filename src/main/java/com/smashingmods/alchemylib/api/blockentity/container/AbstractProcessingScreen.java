package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.data.AbstractDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.DisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.EnergyDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.FluidDisplayData;
import com.smashingmods.alchemylib.api.blockentity.container.data.ProgressDisplayData;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.LinkedList;
import java.util.List;

import static com.smashingmods.alchemylib.api.blockentity.container.Direction2D.*;

/**
 * Heavy lifting for rendering an {@link AbstractProcessingMenu} to screen: backgrounds,
 * energy/fluid/progress display data, fluid-tank textures, directional blits, and tooltips.
 */
@SuppressWarnings("unused")
public abstract class AbstractProcessingScreen<M extends AbstractProcessingMenu> extends AbstractContainerScreen<M> {

    private static final ResourceLocation WIDGETS = ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "textures/gui/widgets.png");

    private final AbstractProcessingBlockEntity blockEntity;
    protected final LinkedList<AbstractWidget> widgets = new LinkedList<>();

    public AbstractProcessingScreen(M pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 184;
        this.imageHeight = 162;
        this.blockEntity = pMenu.getBlockEntity();
    }

    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        super.init();
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        for (int index = 0; index < widgets.size(); index++) {
            renderWidget(widgets.get(index), leftPos - 24, topPos + (index * 24));
        }
    }

    public void drawFluidTank(FluidDisplayData pData) {
        if (pData.getValue() > 0) {
            FluidStack fluidStack = pData.getFluidHandler().getFluidStack();
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            setShaderColor(fluidTypeExtensions.getTintColor(fluidStack));
            TextureAtlasSprite icon = getResourceTexture(fluidTypeExtensions.getStillTexture(fluidStack));
            drawTexture(pData, icon, leftPos + pData.getX(), topPos + pData.getY());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    /**
     * Draws a tiled fluid texture to fill {@code pData}'s rect proportional to its value/max.
     */
    public void drawTexture(AbstractDisplayData pData, TextureAtlasSprite pSprite, int pTextureX, int pTextureY) {
        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);

        int renderAmount = Math.max(Math.min(pData.getHeight(), pData.getValue() * pData.getHeight() / pData.getMaxValue()), 1);
        int posY = pTextureY + pData.getHeight() - renderAmount;

        float minU = pSprite.getU0();
        float maxU = pSprite.getU1();
        float minV = pSprite.getV0();
        float maxV = pSprite.getV1();

        for (int width = 0; width < pData.getWidth(); width += 16) {
            for (int height = 0; height < renderAmount; height += 16) {

                int drawHeight = Math.min(renderAmount - height, 16);
                int drawWidth = Math.min(pData.getWidth() - width, 16);

                int x1 = pTextureX + width;
                float x2 = x1 + drawWidth;
                int y1 = posY + height;
                float y2 = y1 + drawHeight;

                float scaleV = minV + (maxV - minV) * drawHeight / 16f;
                float scaleU = minU + (maxU - minU) * drawWidth / 16f;
                float blitOffset = 0;

                BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.addVertex(x1, y2, blitOffset).setUv(minU, scaleV);
                bufferBuilder.addVertex(x2, y2, blitOffset).setUv(scaleU, scaleV);
                bufferBuilder.addVertex(x2, y1, blitOffset).setUv(scaleU, minV);
                bufferBuilder.addVertex(x1, y1, blitOffset).setUv(minU, minV);
                BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
            }
        }
    }

    public static TextureAtlasSprite getResourceTexture(ResourceLocation pResourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pResourceLocation);
    }

    public static void setShaderColor(int pColor) {
        float alpha = (pColor >> 24 & 255) / 255f;
        float red = (pColor >> 16 & 255) / 255f;
        float green = (pColor >> 8 & 255) / 255f;
        float blue = (pColor & 255) / 255f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public static int getScaled(int pPixels, int pValue, int pMaxValue) {
        if (pValue > 0 && pMaxValue > 0) {
            return pValue * pPixels / pMaxValue;
        }
        return 0;
    }

    public void drawEnergyBar(GuiGraphics pGuiGraphics, EnergyDisplayData pData) {
        int x = pData.getX() + (this.width - this.imageWidth) / 2;
        int y = pData.getY() + (this.height - this.imageHeight) / 2;
        directionalBlit(pGuiGraphics, x, y + pData.getHeight(), 0, 0, pData.getWidth(), pData.getHeight(), pData.getValue(), pData.getMaxValue(), UP, true);
    }

    private void directionalBlit(GuiGraphics pGuiGraphics, int pX, int pY, int pUOffset, int pVOffset, int pU, int pV, int pValue, int pMaxValue, Direction2D pDirection2D) {
        directionalBlit(pGuiGraphics, pX, pY, pUOffset, pVOffset, pU, pV, pValue, pMaxValue, pDirection2D, false);
    }

    private void directionalBlit(GuiGraphics pGuiGraphics, int pX, int pY, int pUOffset, int pVOffset, int pU, int pV, int pValue, int pMaxValue, Direction2D pDirection2D, boolean pScaleOffset) {
        int x = pX;
        int y = pY;
        int uOffset = pUOffset;
        int vOffset = pVOffset;
        int uWidth = pU;
        int vHeight = pV;

        int pVScaled = getScaled(pV, pValue, pMaxValue);
        int pVScalePercent = (int) ((pV * 1.8f) - (pVScaled * 1.8f));
        int finalVOffset = pScaleOffset ? pVScalePercent : pVOffset + pV - pVScaled;

        switch (pDirection2D) {
            case LEFT -> {
                x = pX - pVScaled;
                uOffset = pU - pVScaled;
                uWidth = pVScaled;
            }
            case UP -> {
                y = pY - pVScaled;
                vOffset = finalVOffset;
                vHeight = pVScaled;
            }
            case RIGHT -> {
                uWidth = pVScaled;
                vHeight = pU;
            }
            case DOWN -> vHeight = pVScaled;
        }
        pGuiGraphics.blit(WIDGETS, x, y, uOffset, vOffset, uWidth, vHeight);
    }

    public void directionalArrow(GuiGraphics pGuiGraphics, int pX, int pY, ProgressDisplayData pData) {
        int uOffset = 0;
        int vOffset = 99;
        int width = 0;
        int height = 0;
        switch (pData.getDirection()) {
            case LEFT -> {
                height = 9;
                width = 30;
            }
            case UP -> {
                vOffset = vOffset + 18;
                height = 9;
                width = 30;
            }
            case RIGHT -> {
                vOffset = vOffset + 9;
                height = 9;
                width = 30;
            }
            case DOWN -> {
                uOffset = uOffset + 9;
                vOffset = vOffset + 18;
                height = 9;
                width = 30;
            }
        }
        directionalBlit(pGuiGraphics, pX + pData.getX(), pY + pData.getY(), uOffset, vOffset, height, width, pData.getValue(), pData.getMaxValue(), pData.getDirection());
    }

    public void renderDisplayData(List<AbstractDisplayData> pDisplayData, GuiGraphics pGuiGraphics, int pX, int pY) {
        pDisplayData.forEach(data -> {
            if (data instanceof ProgressDisplayData progressData) {
                directionalArrow(pGuiGraphics, pX, pY, progressData);
            }
            if (data instanceof EnergyDisplayData energyData) {
                drawEnergyBar(pGuiGraphics, energyData);
            }
            if (data instanceof FluidDisplayData fluidData) {
                drawFluidTank(fluidData);
            }
        });
    }

    public void renderDisplayTooltip(List<AbstractDisplayData> pDisplayData, GuiGraphics pGuiGraphics, int pX, int pY, int pMouseX, int pMouseY) {
        pDisplayData.stream().filter(data ->
                pMouseX >= data.getX() + pX &&
                        pMouseX <= data.getX() + pX + data.getWidth() &&
                        pMouseY >= data.getY() + pY &&
                        pMouseY <= data.getY() + pY + data.getHeight()
        ).forEach(data -> {
            if (!(data instanceof ProgressDisplayData)) {
                pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(data.toTextComponent()), pMouseX, pMouseY);
            }
        });
    }

    public void renderItemTooltip(GuiGraphics pGuiGraphics, ItemStack pItemStack, MutableComponent pComponent, int pMouseX, int pMouseY) {
        pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, RecipeDisplayUtil.getItemTooltipComponent(pItemStack, pComponent), pMouseX, pMouseY);
    }

    public <W extends GuiEventListener & Renderable & NarratableEntry> void renderWidget(W pWidget, int pX, int pY) {
        if (!renderables.contains(pWidget)) {
            if (pWidget instanceof AbstractWidget widget) {
                widget.setX(pX);
                widget.setY(pY);
            }
            addRenderableWidget(pWidget);
        }
    }

    public AbstractProcessingBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
