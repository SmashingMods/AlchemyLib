package com.smashingmods.alchemylib.client.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.ToggleLockButtonPacket;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public class LockButton extends AbstractAlchemyButton {

    public LockButton(AbstractProcessingScreen<?> pParent) {
        super(pParent, pButton -> {
                    boolean toggleLock = !pParent.getBlockEntity().isRecipeLocked();
                    pParent.getBlockEntity().setRecipeLocked(toggleLock);
                    pParent.getBlockEntity().setChanged();
                    AlchemyLib.getPacketHandler().sendToServer(new ToggleLockButtonPacket(pParent.getBlockEntity().getBlockPos(), toggleLock));
                });
    }

    @Override
    public void renderButton(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
        blit(pPoseStack, x, y, 25 + ((blockEntity.isRecipeLocked() ? 0 : 1) * 20), 0, width, height);
        renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isRecipeLocked() ? MutableComponent.create(new TranslatableContents("alchemylib.container.unlock_recipe")) : MutableComponent.create(new TranslatableContents("alchemylib.container.lock_recipe"));
    }
}
