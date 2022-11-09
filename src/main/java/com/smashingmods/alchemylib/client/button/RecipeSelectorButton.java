package com.smashingmods.alchemylib.client.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.container.button.AbstractAlchemyButton;
import com.smashingmods.alchemylib.api.blockentity.processing.SearchableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.ForgeHooksClient;

@SuppressWarnings("unused")
public class RecipeSelectorButton extends AbstractAlchemyButton {

    public RecipeSelectorButton(AbstractProcessingScreen<?> pParent, Screen pNewScreen) {
        super(pParent, pButton -> {
            if (pParent.getBlockEntity().isRecipeSelectorOpen()) {
                ForgeHooksClient.popGuiLayer(Minecraft.getInstance());
                pParent.getBlockEntity().setRecipeSelectorOpen(false);
            } else {
                pParent.getBlockEntity().setRecipeSelectorOpen(true);
                ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), pNewScreen);
            }
        });
    }

    @Override
    public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            super.renderButton(pPoseStack, pMouseX, pMouseY, pPartialTick);
            boolean open = ((SearchableBlockEntity) parent.getBlockEntity()).isRecipeSelectorOpen();
            int u = open ? 25 : 45;
            int v = open ? 80 : 60;

            blit(pPoseStack, x, y, u, v, width, height);
            renderButtonTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    public Component getMessage() {
        return ((SearchableBlockEntity) parent.getBlockEntity()).isRecipeSelectorOpen() ? new TranslatableComponent("alchemylib.container.close_recipe_select") : new TranslatableComponent("alchemylib.container.open_recipe_select");
    }
}
