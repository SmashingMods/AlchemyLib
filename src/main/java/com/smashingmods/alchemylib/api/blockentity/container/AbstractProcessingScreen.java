package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.data.*;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedList;
import java.util.List;

import static com.smashingmods.alchemylib.api.blockentity.container.Direction2D.*;

/**
 * This abstract class does the heavy lifting setting up the methods for rendering a {@link AbstractProcessingMenu} to the screen.
 *
 * @param <M> The menu that this screen represents.
 */
@SuppressWarnings("unused")
public abstract class AbstractProcessingScreen<M extends AbstractProcessingMenu> extends AbstractContainerScreen<M> {

    private final AbstractProcessingBlockEntity blockEntity;
    protected final LinkedList<AbstractWidget> widgets = new LinkedList<>();

    public AbstractProcessingScreen(M pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 184;
        this.imageHeight = 162;
        this.blockEntity = pMenu.getBlockEntity();
    }

    /**
     * This method initializes the vaues for leftPos and topPos for the screen. Extenders can override this method to set
     * their own values or to initialize other objects. For example, this is where you should add buttons.
     */
    @Override
    protected void init() {
        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;
        super.init();
    }

    /**
     * Implementers should override this method and call super. This sets up the background color overlay, calls renderBG
     * which is set up by extenders, and renders all widgets added to the widgets field.
     */
    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        renderBg(pGuiGraphics, pPartialTick, pMouseX, pMouseY);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);

        for (int index = 0; index < widgets.size(); index++) {
            renderWidget(widgets.get(index), leftPos - 24, topPos + (index * 24));
        }
    }

    /**
     * If an extending screen adds FluidDisplayData to the list of DisplayData field, this will be called to render a fluid
     * tank to the screen. Display data provides a FluidStack for getting the still texture, color, and other values for
     * rendering.
     *
     * @param pData {@link FluidDisplayData}
     *
     * @see AbstractProcessingScreen#renderDisplayData(List, GuiGraphics, int, int)
     */
    public void drawFluidTank(FluidDisplayData pData) {
        if (pData.getValue() > 0) {
            FluidStack fluidStack = pData.getFluidHandler().getFluidStack();
            IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            setShaderColor(fluidTypeExtensions.getTintColor());
            TextureAtlasSprite icon = getResourceTexture(fluidTypeExtensions.getStillTexture());
            drawTexture(pData, icon, leftPos + pData.getX(), topPos + pData.getY());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    /**
     * This method is similar to using blit but manually interacts with the rendering engine to draw a texture to the screen.
     *
     * @param pData {@link AbstractDisplayData} Can pass any implementer of {@link DisplayData}.
     * @param pSprite {@link TextureAtlasSprite}
     * @param pTextureX Integer for the X position of the screen to render from.
     * @param pTextureY Integer for the Y position of the screen to render from.
     */

    //TODO: Discover why FluidStack textures become invisible when picking up an inventory item.
    public void drawTexture(AbstractDisplayData pData, TextureAtlasSprite pSprite, int pTextureX, int pTextureY) {

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        int renderAmount = Math.max(Math.min(pData.getHeight(), pData.getValue() * pData.getHeight() / pData.getMaxValue()), 1);
        int posY = pTextureY + pData.getHeight() - renderAmount;

        float minU = pSprite.getU0();
        float maxU = pSprite.getU1();
        float minV = pSprite.getV0();
        float maxV = pSprite.getV1();

        for (int width = 0; width < pData.getWidth(); width++) {
            for (int height = 0; height < pData.getHeight(); height++) {

                int drawHeight = Math.min(renderAmount - height, 16);
                int drawWidth = Math.min(pData.getWidth() - width, 16);

                int x1 = pTextureX + width;
                float x2 = x1 + drawWidth;
                int y1 = posY + height;
                float y2 = y1 + drawHeight;

                float scaleV = minV + (maxV - minV) * drawHeight / 16f;
                float scaleU = minU + (maxU - minU) * drawWidth / 16f;

                float blitOffset = 0;

                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.getBuilder();

                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                bufferBuilder.vertex(x1, y2, blitOffset).uv(minU, scaleV).endVertex();
                bufferBuilder.vertex(x2, y2, blitOffset).uv(scaleU, scaleV).endVertex();
                bufferBuilder.vertex(x2, y1, blitOffset).uv(scaleU, minV).endVertex();
                bufferBuilder.vertex(x1, y1, blitOffset).uv(minU, minV).endVertex();
                tesselator.end();

                height += 15;
            }
            width += 16;
        }
    }

    /**
     * Gets a texture from {@link InventoryMenu#BLOCK_ATLAS} by the passed ResourceLocation.
     *
     * @param pResourceLocation {@link ResourceLocation}
     * @return {@link TextureAtlasSprite}
     */
    public static TextureAtlasSprite getResourceTexture(ResourceLocation pResourceLocation) {
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(pResourceLocation);
    }

    /**
     * Sets the RenderSystem's shader color by converting an integer color to an RGBA value using bit shifting. The shader
     * color needs to be set back to default (1.0F, 1.0F, 1.0F, 1.0F) after rendering the color.
     *
     * @param pColor Interger color value.
     */
    public static void setShaderColor(int pColor) {
        float alpha = (pColor >> 24 & 255) / 255f;
        float red = (pColor >> 16 & 255) / 255f;
        float green = (pColor >> 8 & 255) / 255f;
        float blue = (pColor & 255) / 255f;
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    /**
     * @param pPixels Integer value of the height of the area being drawn to. For example, if you are drawing an energy
     *                bar to the screen inside a 60 pixel height area, you pass 60 here.
     * @param pValue {@link DisplayData#getValue()}
     * @param pMaxValue {@link DisplayData#getMaxValue()}
     * @return Integer for the scaled hieght to draw.
     */
    public static int getScaled(int pPixels, int pValue, int pMaxValue) {
        if (pValue > 0 && pMaxValue > 0) {
            return pValue * pPixels / pMaxValue;
        } else {
            return 0;
        }
    }

    /**
     * This helper method sets up the values from EnergyDisplayData to call directional blit.
     *
     * @param pData {@link EnergyDisplayData}
     */
    public void drawEnergyBar(GuiGraphics pGuiGraphics, EnergyDisplayData pData) {
        int x = pData.getX() + (this.width - this.imageWidth) / 2;
        int y = pData.getY() + (this.height - this.imageHeight) / 2;
        directionalBlit(pGuiGraphics, x, y + pData.getHeight(), 0, 0, pData.getWidth(), pData.getHeight(), pData.getValue(), pData.getMaxValue(), UP, true);
    }

    /**
     * Overload for directionalBlit with scale offset set to false.
     */
    private void directionalBlit(GuiGraphics pGuiGraphics, int pX, int pY, int pUOffset, int pVOffset, int pU, int pV, int pValue, int pMaxValue, Direction2D pDirection2D) {
        directionalBlit(pGuiGraphics, pX, pY, pUOffset, pVOffset, pU, pV, pValue, pMaxValue, pDirection2D, false);
    }

    /**
     * Renders a texture from a texture atlas to the screen using a blit method.
     *
     * @param pX Integer value of X position to render to the screen.
     * @param pY Integer value of Y position to render to the screen.
     * @param pUOffset Integer value of uOffset (left to right) for a texture atlas. Correlates to width.
     * @param pVOffset Integer value of vOffset (top to bottom) for a texture atlas. Correlates to height.
     * @param pU Integer value of U for a texture atlas.
     * @param pV Integer value of V for a teture atlas.
     * @param pValue Integer for current value typically of DisplayData. For example, an energy bar displaying 10,000 FE.
     * @param pMaxValue Integer for max value typically of DisplayData. For example, an energy bar with a capacity of 100,000 FE.
     * @param pDirection2D {@link Direction2D} for the direction to blit to the screen. Direction is toward, so if Left,
     *                                        blitting will start on the right and move toward the left.
     * @param pScaleOffset Calculated value for offsetting the scale. This is used primarily for changing the color of the
     *                     energy bar based on its value. If the energy bar has a lot of energy, it will be greener, and if
     *                     it's low on energy, it will be redder. The actual texture covers the entire spectrum and this value
     *                     is changing where on the texture is being rendered to the screen.
     */
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
                x = pX -pVScaled;
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
        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), x, y, uOffset, vOffset, uWidth, vHeight);
    }

    /**
     * {@link ProgressDisplayData} holds a {@link Direction2D} for defining the direction on the screen to render the
     * progress arrow. Progress arrows are hard coded such that their height and width are 9/30 respectively or rotated
     * based on that. Arrow textures live on the widgets.png starting at uOffset 0 and vOffset 100.
     *
     * @param pData {@link ProgressDisplayData}
     */
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

    /**
     * Calls the relevant rendering method depending on the type of {@link AbstractDisplayData}.
     */
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

    /**
     * Tests the location of the mouse position against the X/Y positions of all display data objects held by this screen
     * to render a tooltip calling {@link DisplayData#toTextComponent()}.
     */
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

    /**
     * Helper method that calls {@link RecipeDisplayUtil#getItemTooltipComponent(ItemStack, MutableComponent)} for a given ItemStack
     * and MutableComponent to render to the screen.
     */
    public void renderItemTooltip(GuiGraphics pGuiGraphics, ItemStack pItemStack, MutableComponent pComponent, int pMouseX, int pMouseY) {
        pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, RecipeDisplayUtil.getItemTooltipComponent(pItemStack, pComponent), pMouseX, pMouseY);
    }

    /**
     *  Call this method to add a widget (button) to the list of renderable widgets. This makes sure that a widget is
     *  only added once as adding multiple copies of the same widget will cause them all to be rendered even if you can't
     *  see them which is very bad for game FPS.
     *
     *  <p>Extenders of {@link AbstractWidget} can have their X/Y screen positions set while other types of widgets might
     *  handle positioning differently.</p>
     *
     * @param pWidget Extends {@link GuiEventListener} &amp; {@link Renderable} &amp; {@link NarratableEntry}
     * @param pX Integer for X position on the screen.
     * @param pY Integer for Y position on the screen.
     * @param <W> extends GuiEventListener &amp; Widget &amp; NarratableEntry
     *
     * @see AbstractWidget
     * @see com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton AbstractAlchemyButton
     */
    public <W extends GuiEventListener & Renderable & NarratableEntry> void renderWidget(W pWidget, int pX, int pY) {
        if (!renderables.contains(pWidget)) {
            if (pWidget instanceof AbstractWidget widget) {
                widget.setX(pX);
                widget.setY(pY);
            }
            addRenderableWidget(pWidget);
        }
    }

    /**
     * @return A reference to the menu's reference to the block entity.
     */
    public AbstractProcessingBlockEntity getBlockEntity() {
        return blockEntity;
    }
}
