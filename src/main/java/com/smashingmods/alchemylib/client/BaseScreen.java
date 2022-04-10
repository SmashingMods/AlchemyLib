package com.smashingmods.alchemylib.client;

import com.smashingmods.alchemylib.container.BaseContainer;
import com.smashingmods.alchemylib.tiles.GuiTile;
import com.smashingmods.alchemylib.utils.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseScreen<T extends BaseContainer> extends AbstractContainerScreen<T> {
    protected ResourceLocation GUI;

    T screenContainer;
    protected final List<CapabilityDisplayWrapper> displayData = new ArrayList<>();

    ResourceLocation powerBarTexture;

    public BaseScreen(String modid, T screenContainer, Inventory inv, Component name, String path) {
        super(screenContainer, inv, name);
        powerBarTexture = new ResourceLocation("alib", "textures/gui/template.png");
        this.screenContainer = screenContainer;
        this.imageWidth = ((GuiTile) screenContainer.tile).getWidth();
        this.imageHeight = ((GuiTile) screenContainer.tile).getHeight();
        GUI = new ResourceLocation(modid, path);
    }

    //drawGuiContainerBackgroundLayer->func_230459_a_
    @Override
    protected void renderBg(PoseStack ps, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        //this.getMinecraft().getTextureManager().bindForSetup(GUI);
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(ps, relX, relY, 0, 0, this.imageWidth, this.imageHeight);


    }

    //drawGuiContainerForegroundLayer->func_230451b
    @Override
    protected void renderLabels(PoseStack ps, int mouseX, int mouseY) {
        //super.renderLabels(ps, mouseX, mouseY);
        String displayName = this.screenContainer.tile.getDisplayName().getString();
        this.drawString(ps, this.font, displayName,
                this.imageWidth / 2 - this.font.width(displayName) / 2, -10, Color.WHITE.getRGB());
    }

    @Override
    public void render(PoseStack ps, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ps);
        super.render(ps, mouseX, mouseY, partialTicks);
        //renderHoveredToolTip->func_230459_a_
        this.renderTooltip(ps, mouseX, mouseY);


        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;

        displayData.forEach(data -> {
            if (data instanceof CapabilityEnergyDisplayWrapper) {
                this.drawPowerBar(ps, (CapabilityEnergyDisplayWrapper) data, powerBarTexture, 0, 0);
            } else if (data instanceof CapabilityFluidDisplayWrapper) {
                this.drawFluidTank((CapabilityFluidDisplayWrapper) data, x + data.x, y + data.y);
            }
        });
        this.displayData.stream().filter(data -> (mouseX >= data.x + x
                && mouseX <= data.x + x + data.width
                && mouseY >= data.y + y
                && mouseY <= data.y + y + data.height))
                .forEach(it -> renderTooltip(ps, it.toTextComponent(), mouseX, mouseY));
    }

    public int getBarScaled(int pixels, int count, int max) {
        if (count > 0 && max > 0) return count * pixels / max;
        else return 0;
    }

    public void drawPowerBar(PoseStack ps, CapabilityEnergyDisplayWrapper data,
                             ResourceLocation texture, int textureX, int textureY) {
        if (data.getStored() > 0) {
            int i = data.x + ((this.width - this.imageWidth) / 2);
            int j = data.y + ((this.height - this.imageHeight) / 2);
            int k = this.getBarScaled(data.height, data.getStored(), data.getCapacity());
            RenderSystem.setShaderTexture(0, texture);
            //this.getMinecraft().textureManager.bindForSetup(texture);
            this.blit(ps, i, j + data.height - k, textureX, textureY, data.width, k);
            //this.getMinecraft().textureManager.bindForSetup(this.GUI);
            RenderSystem.setShaderTexture(0, this.GUI);
        }
    }

    public void drawFluidTank(CapabilityFluidDisplayWrapper wrapper, int i, int j) {
        drawFluidTank(wrapper, i, j, 16, 60);
    }

    public void drawFluidTank(CapabilityFluidDisplayWrapper wrapper, int i, int j, int width, int height) {
        if (wrapper.getStored() > 5) {
            RenderUtils.bindBlockTexture();
            RenderUtils.renderGuiTank(wrapper.getHandler().getFluidInTank(0), wrapper.getCapacity(),
                    wrapper.getStored(), i, j, getBlitOffset(), width, height);
        }
    }

    public void bindWidgets() {
        //getMinecraft().textureManager.bindForSetup
        RenderSystem.setShaderTexture(0, new ResourceLocation("alib", "textures/gui/widgets.png"));
    }

    public void drawRightArrow(PoseStack ps, int x, int y, int width) {
        int height = 9;
        bindWidgets();
        blit(ps, x, y, 0, 120, width, height);
    }

    public void drawDownArrow(PoseStack ps, int x, int y, int height) {
        int width = 9;
        bindWidgets();
        blit(ps, x, y, 9, 129, width, height);
    }

    public void drawUpArrow(PoseStack ps, int x, int y, int height) {
        int width = 9;
        bindWidgets();
        blit(ps, x, y, 0, 129, width, height);
    }
}