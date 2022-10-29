package com.smashingmods.alchemylib.api.blockentity.container.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.TogglePauseButtonPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import javax.annotation.Nonnull;

public class PauseButton extends AbstractAlchemyButton {

    public PauseButton(AbstractProcessingScreen<?> pParent, AbstractProcessingBlockEntity pBlockEntity) {
        super(pParent, pBlockEntity, pButton -> {
            boolean togglePause = !pBlockEntity.isProcessingPaused();
            pBlockEntity.setPaused(!togglePause);
            pBlockEntity.setChanged();
            AlchemyLib.getPacketHandler().sendToServer(new TogglePauseButtonPacket(pBlockEntity.getBlockPos(), togglePause));
        });
    }

    @Override
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 25 + ((blockEntity.isProcessingPaused() ? 1 : 0) * 20), 20, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return blockEntity.isProcessingPaused() ? new TranslatableComponent("alchemylib.container.resume") : new TranslatableComponent("alchemylib.container.pause");
    }
}
