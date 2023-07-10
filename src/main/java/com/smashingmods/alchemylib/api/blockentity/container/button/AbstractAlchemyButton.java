package com.smashingmods.alchemylib.api.blockentity.container.button;

import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Extend this class to make a Button to render to the screen using {@link AbstractProcessingScreen}.
 */
@SuppressWarnings("unused")
public abstract class AbstractAlchemyButton extends Button {

    protected final AbstractProcessingScreen<?> parent;
    protected final AbstractProcessingBlockEntity blockEntity;

    /**
     *  This convenience constructor sets up default values for creating a new button. Use
     *  the other constructor if you want to set your own values.
     *
     * @param pParent {@link AbstractProcessingScreen}
     * @param pOnPress {@link Button#onPress}
     */
    public AbstractAlchemyButton(AbstractProcessingScreen<?> pParent, Button.OnPress pOnPress) {
        this(0, 0, 20, 20, MutableComponent.create(new LiteralContents("")), pParent, pOnPress);
    }

    /**
     *  Button widget optimized for rendering to AlchemyLib screens.
     *
     * @param pX Starting X position on the screen for drawing this button.
     * @param pY Starting Y position on the screen for drawing this button.
     * @param pWidth Width of this button. Helper constructor defaults to 20.
     * @param pHeight Height of this button. Helper constructor defaults to 20.
     * @param pComponent {@link MutableComponent} for displaying on the button. By default, this is set to an empty string and no text is displayed.
     * @param pParent {@link AbstractProcessingScreen} the screen that this button is rendered on.
     * @param pOnPress {@link Button#onPress} the callback method to be executed when the button is pressed.
     */
    public AbstractAlchemyButton(int pX, int pY, int pWidth, int pHeight, MutableComponent pComponent, AbstractProcessingScreen<?> pParent, Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pComponent, pOnPress, DEFAULT_NARRATION);
        this.parent = pParent;
        this.blockEntity = pParent.getBlockEntity();
    }

    /**
     * Renders the button's tooltip defined in its constructor to the parent screen.
     */
    public void renderButtonTooltip(@Nonnull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (pMouseX >= getX() && pMouseX <= getX() + width && pMouseY >= getY() && pMouseY <= getY() + height) {
            pGuiGraphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(getMessage()), pMouseX, pMouseY);
        }
    }
}
