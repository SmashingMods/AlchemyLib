package com.smashingmods.alchemylib.client.button;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.TogglePauseButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class PauseButton extends AbstractAlchemyButton {

    public PauseButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            boolean togglePause = !pParent.getBlockEntity().isProcessingPaused();
            pParent.getBlockEntity().setPaused(!togglePause);
            pParent.getBlockEntity().setChanged();
            AlchemyLib.getPacketHandler().sendToServer(new TogglePauseButtonPacket(pParent.getBlockEntity().getBlockPos(), togglePause));
        });
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 25 + ((blockEntity.isProcessingPaused() ? 1 : 0) * 20), 20, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isProcessingPaused() ? MutableComponent.create(new TranslatableContents("alchemylib.container.resume", null, TranslatableContents.NO_ARGS)) : MutableComponent.create(new TranslatableContents("alchemylib.container.pause", null, TranslatableContents.NO_ARGS));
    }
}
