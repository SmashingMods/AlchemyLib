package al132.alib.client;

import al132.alib.ModData;
import al132.alib.container.ABaseContainer;
import al132.alib.tiles.GuiTile;
import al132.alib.utils.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ABaseScreen<T extends ABaseContainer> extends ContainerScreen<T> {
    protected ResourceLocation GUI;

    T screenContainer;
    protected List<CapabilityDisplayWrapper> displayData = new ArrayList<>();

    ResourceLocation powerBarTexture;

    public ABaseScreen(ModData data, T screenContainer, PlayerInventory inv, ITextComponent name, String path) {
        super(screenContainer, inv, name);
        powerBarTexture = new ResourceLocation(data.MODID, "textures/gui/template.png");
        this.screenContainer = screenContainer;
        this.xSize = ((GuiTile) screenContainer.tile).getWidth();
        this.ySize = ((GuiTile) screenContainer.tile).getHeight();
        GUI = new ResourceLocation(data.MODID, path);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(relX, relY, 0, 0, this.xSize, this.ySize);


        displayData.forEach(data -> {
            if (data instanceof CapabilityEnergyDisplayWrapper) {
                this.drawPowerBar((CapabilityEnergyDisplayWrapper) data, powerBarTexture, 0, 0);
            } else if (data instanceof CapabilityFluidDisplayWrapper) {
                this.drawFluidTank((CapabilityFluidDisplayWrapper) data, relX + data.x, relY + data.y);
            }
        });
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String displayName = this.screenContainer.tile.getDisplayName().getString();
        this.drawString(this.font, displayName,
                this.xSize / 2 - this.font.getStringWidth(displayName) / 2, -10, Color.WHITE.getRGB());
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        this.displayData.stream().filter(data -> (mouseX >= data.x + x
                && mouseX <= data.x + x + data.width
                && mouseY >= data.y + y
                && mouseY <= data.y + y + data.height))
                .forEach(it -> renderTooltip(it.toStringList(), mouseX, mouseY, font));
    }

    public int getBarScaled(int pixels, int count, int max) {
        if (count > 0 && max > 0) return count * pixels / max;
        else return 0;
    }

    public void drawPowerBar(CapabilityEnergyDisplayWrapper storage,
                             ResourceLocation texture, int textureX, int textureY) {
        if (storage.getStored() > 0) {
            int i = storage.x + ((this.width - this.xSize) / 2);
            int j = storage.y + ((this.height - this.ySize) / 2);
            int k = this.getBarScaled(storage.height, storage.getStored(), storage.getCapacity());

            this.minecraft.textureManager.bindTexture(texture);
            this.blit(i, j + storage.height - k, textureX, textureY, storage.width, k);
            this.minecraft.textureManager.bindTexture(this.GUI);
        }
    }

    public void drawFluidTank(CapabilityFluidDisplayWrapper wrapper, int i, int j) {
        drawFluidTank(wrapper, i, j, 16, 60);
    }

    public void drawFluidTank(CapabilityFluidDisplayWrapper wrapper, int i, int j, int width, int height) {
        if (wrapper.getStored() > 5) {
            RenderUtils.bindBlockTexture();
            RenderUtils.renderGuiTank(wrapper.fluid.get().getFluidInTank(0), wrapper.getCapacity(),
                    wrapper.getStored(), i, j, blitOffset, width, height);
        }
    }
}