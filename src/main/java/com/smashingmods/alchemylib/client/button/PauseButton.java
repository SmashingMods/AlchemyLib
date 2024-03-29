package com.smashingmods.alchemylib.client.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.TogglePauseButtonPacket;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

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
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 25 + ((blockEntity.isProcessingPaused() ? 1 : 0) * 20), 20, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isProcessingPaused() ? MutableComponent.create(new TranslatableContents("alchemylib.container.resume")) : MutableComponent.create(new TranslatableContents("alchemylib.container.pause"));
    }
}
