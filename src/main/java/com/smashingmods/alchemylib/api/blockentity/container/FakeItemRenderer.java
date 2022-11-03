package com.smashingmods.alchemylib.api.blockentity.container;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.smashingmods.chemlib.ChemLib;
import com.smashingmods.chemlib.common.items.ChemicalItem;
import com.smashingmods.chemlib.common.items.ElementItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class FakeItemRenderer {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static final ItemRenderer ITEM_RENDERER = MINECRAFT.getItemRenderer();
    private static final TextureManager TEXTURE_MANAGER = MINECRAFT.getTextureManager();

    public static void renderFakeItem(ItemStack pItemStack, int pX, int pY, float pAlpha) {

        if (!pItemStack.isEmpty()) {
            BakedModel model = getBakedModel(pItemStack);

            TEXTURE_MANAGER.getTexture(InventoryMenu.BLOCK_ATLAS).setFilter(true, false);
            RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.enableBlend();

            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(pX + 8.0D, pY + 8.0D, 0F);
            modelViewStack.scale(16.0F, -16.0F, 16.0F);
            RenderSystem.applyModelViewMatrix();

            if (!model.usesBlockLight()) {
                Lighting.setupForFlatItems();
            }

            MultiBufferSource.BufferSource bufferSource = MINECRAFT.renderBuffers().bufferSource();
            ITEM_RENDERER.render(pItemStack,
                    ItemTransforms.TransformType.GUI,
                    false,
                    new PoseStack(),
                    getWrappedBuffer(bufferSource, pAlpha),
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    model);
            bufferSource.endBatch();
            RenderSystem.enableDepthTest();

            if (!model.usesBlockLight()) {
                Lighting.setupFor3DItems();
            }

            modelViewStack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    private static MultiBufferSource getWrappedBuffer(MultiBufferSource pBufferSource, float pAlpha) {
        return pRenderType -> new WrappedVertexConsumer(pBufferSource.getBuffer(RenderType.entityTranslucent(InventoryMenu.BLOCK_ATLAS)), 1F, 1F, 1F, pAlpha);
    }

    private static BakedModel getBakedModel(ItemStack pItemStack) {
        ModelResourceLocation chemicalModel = getChemicalModel(pItemStack);
        return chemicalModel != null ? ITEM_RENDERER.getItemModelShaper().getModelManager().getModel(chemicalModel) : ITEM_RENDERER.getModel(pItemStack, null, MINECRAFT.player, 0);
    }

    @Nullable
    private static ModelResourceLocation getChemicalModel(ItemStack pItemStack) {
        ModelResourceLocation modelResourceLocation = null;
        if (pItemStack.getItem() instanceof ElementItem elementItem) {
            switch (elementItem.getMatterState()) {
                case LIQUID -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_liquid_model"), "inventory");
                case GAS -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_gas_model"), "inventory");
                default -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "element_solid_model"), "inventory");
            }
        } else if (pItemStack.getItem() instanceof ChemicalItem chemicalItem) {
            switch (chemicalItem.getItemType()) {
                case DUST -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_dust_model"), "inventory");
                case NUGGET -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_nugget_model"), "inventory");
                case INGOT -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_ingot_model"), "inventory");
                case PLATE -> modelResourceLocation = new ModelResourceLocation(new ResourceLocation(ChemLib.MODID, "chemical_plate_model"), "inventory");
            }
        }
        return modelResourceLocation;
    }
}

class WrappedVertexConsumer implements VertexConsumer {

    protected final VertexConsumer consumer;
    protected final float red;
    protected final float green;
    protected final float blue;
    protected final float alpha;

    public WrappedVertexConsumer(VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha) {
        this.consumer = pConsumer;
        this.red = pRed;
        this.green = pGreen;
        this.blue = pBlue;
        this.alpha = pAlpha;
    }

    @Override
    public VertexConsumer vertex(double pX, double pY, double pZ) {
        return consumer.vertex(pX, pY, pZ);
    }

    @Override
    public VertexConsumer color(int pRed, int pGreen, int pBlue, int pAlpha) {
        return consumer.color((int)(pRed * red), (int)(pGreen * green), (int)(pBlue * blue), (int)(pAlpha * alpha));
    }

    @Override
    public VertexConsumer uv(float pU, float pV) {
        return consumer.uv(pU, pV);
    }

    @Override
    public VertexConsumer overlayCoords(int pU, int pV) {
        return consumer.overlayCoords(pU, pV);
    }

    @Override
    public VertexConsumer uv2(int pU, int pV) {
        return consumer.uv2(pU, pV);
    }

    @Override
    public VertexConsumer normal(float pX, float pY, float pZ) {
        return consumer.normal(pX, pY, pZ);
    }

    @Override
    public void endVertex() {
        consumer.endVertex();
    }

    @Override
    public void defaultColor(int pDefaultR, int pDefaultG, int pDefaultB, int pDefaultA) {
        consumer.defaultColor(pDefaultR, pDefaultG, pDefaultB, pDefaultA);
    }

    @Override
    public void unsetDefaultColor() {
        consumer.unsetDefaultColor();
    }
}
