package com.smashingmods.alchemylib.client.button;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.common.network.ToggleLockButtonPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;

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
    public void renderWidget(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        pGuiGraphics.blit(new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png"), getX(), getY(), 25 + ((blockEntity.isRecipeLocked() ? 0 : 1) * 20), 0, width, height);
        renderButtonTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    public MutableComponent getMessage() {
        return blockEntity.isRecipeLocked() ? MutableComponent.create(new TranslatableContents("alchemylib.container.unlock_recipe", null, TranslatableContents.NO_ARGS)) : MutableComponent.create(new TranslatableContents("alchemylib.container.lock_recipe", null, TranslatableContents.NO_ARGS));
    }
}
