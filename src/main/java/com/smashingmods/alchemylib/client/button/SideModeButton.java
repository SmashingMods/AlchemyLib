package com.smashingmods.alchemylib.client.button;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.api.blockentity.processing.ProcessingBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.SearchableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public class SideModeButton extends AbstractAlchemyButton {

    public SideModeButton(AbstractProcessingScreen<?> pParent, Screen pNewScreen) {
        super(pParent, pButton -> {
            ProcessingBlockEntity blockEntity = pParent.getBlockEntity();
            if (blockEntity.isSideConfigScreenOpen()) {
                Minecraft.getInstance().popGuiLayer();
                blockEntity.setSideConfigScreenState(false);
            } else {
                if (!(blockEntity instanceof SearchableBlockEntity searchableBlockEntity) || !searchableBlockEntity.isRecipeSelectorOpen()) {
                    blockEntity.setSideConfigScreenState(true);
                    Minecraft.getInstance().pushGuiLayer(pNewScreen);
                }
            }
        });
    }

    @Override
    public void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        boolean open = ((ProcessingBlockEntity) parent.getBlockEntity()).isSideConfigScreenOpen();
        int u = open ? 25 : 85;
        int v = open ? 80 : 0;

        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), u, v, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return MutableComponent.create(new TranslatableContents("alchemistry.container.sides.button", "Input/Output Configuration", TranslatableContents.NO_ARGS));
    }
}
