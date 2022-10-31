package com.smashingmods.alchemylib.api.blockentity.container.button;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

public abstract class AbstractAlchemyButton extends Button {

    protected final AbstractProcessingScreen<?> parent;
    protected final AbstractProcessingBlockEntity blockEntity;

    public AbstractAlchemyButton(AbstractProcessingScreen<?> pParent, AbstractProcessingBlockEntity pBlockEntity, Button.OnPress pOnPress) {
        this(0, 0, 20, 20, MutableComponent.create(new LiteralContents("")), pParent, pBlockEntity, pOnPress);
    }

    public AbstractAlchemyButton(int pX, int pY, int pWidth, int pHeight, MutableComponent pComponent, AbstractProcessingScreen<?> pParent, AbstractProcessingBlockEntity pBlockEntity, Button.OnPress pOnPress) {
        super(pX, pY, pWidth, pHeight, pComponent, pOnPress);
        this.parent = pParent;
        this.blockEntity = pBlockEntity;
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        ResourceLocation buttonTexture = new ResourceLocation(AlchemyLib.MODID, "textures/gui/widgets.png");

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, buttonTexture);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
    }

    public void renderButtonTooltip(@Nonnull PoseStack pPoseStack, int pMouseX, int pMouseY) {
        if (pMouseX >= x && pMouseX <= x + width && pMouseY >= y && pMouseY <= y + height) {
            parent.renderTooltip(pPoseStack, getMessage(), pMouseX, pMouseY);
        }
    }
}
