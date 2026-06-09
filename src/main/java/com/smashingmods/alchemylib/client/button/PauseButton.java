package com.smashingmods.alchemylib.client.button;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.TogglePauseButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class PauseButton extends AbstractAlchemyButton {

    public PauseButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
            boolean paused = !pParent.getBlockEntity().isProcessingPaused();
            pParent.getBlockEntity().setPaused(paused);
            pParent.getBlockEntity().setChanged();
            AlchemyLib.getPacketHandler().sendToServer(new TogglePauseButtonPacket(pParent.getBlockEntity().getBlockPos(), paused));
        });
    }

    @Override
    public void renderWidget(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        pGuiGraphics.blit(RenderType::guiTextured, ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 25 + ((blockEntity.isProcessingPaused() ? 1 : 0) * 20), 20, width, height, 256, 256);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isProcessingPaused() ?
                MutableComponent.create(new TranslatableContents("alchemylib.container.resume", "Resume", TranslatableContents.NO_ARGS))
                :
                MutableComponent.create(new TranslatableContents("alchemylib.container.pause", "Pause", TranslatableContents.NO_ARGS));
    }
}
