package com.smashingmods.alchemylib.api.blockentity.container.button;

import com.mojang.blaze3d.vertex.PoseStack;
import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingScreen;
import com.smashingmods.alchemylib.api.blockentity.processing.SearchableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraftforge.client.ForgeHooksClient;

@SuppressWarnings({"unused", "UnstableApiUsage"})
public class RecipeSelectorButton extends AbstractAlchemyButton {

    public RecipeSelectorButton(AbstractProcessingScreen<?> pParent, Screen pNewScreen) {
        super(pParent, pParent.getBlockEntity(), pButton -> {
            if (pParent.getBlockEntity() instanceof SearchableBlockEntity searchableBlockEntity) {
                if (searchableBlockEntity.isRecipeSelectorOpen()) {
                    ForgeHooksClient.popGuiLayer(Minecraft.getInstance());
                    searchableBlockEntity.setRecipeSelectorOpen(false);
                } else {
                    searchableBlockEntity.setRecipeSelectorOpen(true);
                    ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), pNewScreen);
                }
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
    public MutableComponent getMessage() {
        return ((SearchableBlockEntity) parent.getBlockEntity()).isRecipeSelectorOpen() ? MutableComponent.create(new TranslatableContents("alchemylib.container.close_recipe_select")) : MutableComponent.create(new TranslatableContents("alchemylib.container.open_recipe_select"));
    }
}
