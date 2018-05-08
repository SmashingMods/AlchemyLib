package al132.alib.client


import al132.alib.tiles.ALTile
import al132.alib.tiles.IGuiTile
import al132.alib.utils.RenderUtils
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Container
import net.minecraft.util.ResourceLocation
import java.awt.Color
import java.util.*
import kotlin.reflect.full.companionObjectInstance


abstract class ALGuiBase<T>(container: Container, val tile: T)
    : GuiContainer(container) where T : ALTile, T : IGuiTile {

    var displayData = ArrayList<CapabilityDisplayWrapper>()
    abstract val displayName: String
    open var powerBarX = 0
    open var powerBarY = 0
    open var powerBarTexture: ResourceLocation? = null


    init {
        this.xSize = tile.guiWidth
        this.ySize = tile.guiHeight
    }

    open fun getBarScaled(pixels: Int, count: Int, max: Int): Int {
        if (count > 0 && max > 0) return count * pixels / max
        else return 0
    }

    open fun drawPowerBar(storage: CapabilityEnergyDisplayWrapper,
                          texture: ResourceLocation,
                          textureX: Int, textureY: Int) {
        if (storage.getStored() > 0) {
            val i = storage.x + ((this.width - this.xSize) / 2)
            val j = storage.y + ((this.height - this.ySize) / 2)
            val k = this.getBarScaled(storage.height, storage.getStored(), storage.getCapacity())

            mc.textureManager.bindTexture(texture)
            this.drawTexturedModalRect(i, j + storage.height - k, textureX, textureY, storage.width, k)
            this.mc.textureManager.bindTexture((this::class.companionObjectInstance as IResource).textureLocation())
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, f: Float) {
        super.drawScreen(mouseX, mouseY, f)

        val x = (this.width - this.xSize) / 2
        val y = (this.height - this.ySize) / 2
        this.displayData.filter { data ->
            (mouseX >= data.x + x
                    && mouseX <= data.x + x + data.width
                    && mouseY >= data.y + y
                    && mouseY <= data.y + y + data.height)
        }.forEach { drawHoveringText(it.toStringList(), mouseX, mouseY, fontRenderer) }
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        this.mc.textureManager.bindTexture((this::class.companionObjectInstance as IResource).textureLocation())
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize)
        val i = (this.width - this.xSize) / 2
        val j = (this.height - this.ySize) / 2

        displayData.forEach { data ->
            when (data) {
                is CapabilityEnergyDisplayWrapper -> powerBarTexture?.let {
                    this.drawPowerBar(storage = data,
                            texture = powerBarTexture!!,
                            textureX = powerBarX,
                            textureY = powerBarY)
                }
                is CapabilityFluidDisplayWrapper -> this.drawFluidTank(data, i + data.x, j + data.y)
            }
        }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY)
        if (this.displayName.isNotEmpty()) {
            this.fontRenderer.drawString(this.displayName,
                    this.xSize / 2 - this.fontRenderer.getStringWidth(this.displayName) / 2, -10, Color.WHITE.rgb)
        }
    }


    open fun drawFluidTank(wrapper: CapabilityFluidDisplayWrapper, i: Int, j: Int, width: Int = 16, height: Int = 60) {
        if (wrapper.getStored() > 5) {
            RenderUtils.bindBlockTexture()
            RenderUtils.renderGuiTank(wrapper.getFluid(), wrapper.getCapacity(),
                    wrapper.getStored(), i.toDouble(), j.toDouble(), zLevel.toDouble(), width.toDouble(), height.toDouble())
        }
    }

    companion object : IResource {
        override fun textureLocation(): ResourceLocation? = null
    }
}

interface IResource {
    fun textureLocation(): ResourceLocation?
}