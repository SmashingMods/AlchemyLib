package com.smashingmods.alchemylib.api.blockentity.container.button;

import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Button base class for {@link AbstractProcessingScreen}-rendered widgets.
 */
@SuppressWarnings("unused")
public abstract class AbstractAlchemyButton extends Button {

    protected final AbstractProcessingScreen<?> parent;
    protected final AbstractProcessingBlockEntity blockEntity;

    public AbstractAlchemyButton(AbstractProcessingScreen<?> pParent, Button.OnPress pOnPress) {
        this(0, 0, 20, 20, Component.empty(), pParent, pOnPress);
    }

    public AbstractAlchemyButton(int pX, int pY, int pWidth, int pHeight, MutableComponent pComponent, AbstractProcessingScreen<?> pParent, Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pComponent, pOnPress, DEFAULT_NARRATION);
        this.parent = pParent;
        this.blockEntity = pParent.getBlockEntity();
    }

    public void renderButtonTooltip(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (pMouseX >= getX() && pMouseX <= getX() + width && pMouseY >= getY() && pMouseY <= getY() + height) {
            pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(getMessage()), pMouseX, pMouseY);
        }
    }
}
