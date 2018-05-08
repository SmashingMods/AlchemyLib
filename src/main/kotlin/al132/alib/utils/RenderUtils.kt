package al132.alib.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.texture.TextureManager
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidTank
import org.lwjgl.opengl.GL11


//From endercore, Creative Commons license, https://github.com/SleepyTrousers/EnderCore
object RenderUtils {

    val BLOCK_TEX = TextureMap.LOCATION_BLOCKS_TEXTURE

    fun engine(): TextureManager = Minecraft.getMinecraft().renderEngine

    fun bindBlockTexture() = engine().bindTexture(BLOCK_TEX)

    fun bindTexture(string: String) = engine().bindTexture(ResourceLocation(string))

    fun bindTexture(tex: ResourceLocation) = engine().bindTexture(tex)


    fun getStillTexture(fluid: FluidStack?): TextureAtlasSprite? {
        return fluid?.fluid?.let{return getStillTexture(it)}
    }

    fun getStillTexture(fluid: Fluid): TextureAtlasSprite? {
        val iconKey = fluid.still ?: return null
        return Minecraft.getMinecraft().textureMapBlocks.getTextureExtry(iconKey.toString())
    }

    fun renderGuiTank(tank: FluidTank, x: Double, y: Double, zLevel: Double, width: Double, height: Double) {
        renderGuiTank(tank.fluid, tank.capacity, tank.fluidAmount, x, y, zLevel, width, height)
    }

    fun renderGuiTank(fluid: FluidStack?, capacity: Int, amount: Int, x: Double, y: Double, zLevel: Double, width: Double, height: Double) {
        if (fluid == null || fluid.fluid == null || fluid.amount <= 0) return

        val icon = getStillTexture(fluid) ?: return

        val renderAmount = Math.max(Math.min(height, amount * height / capacity), 1.0).toInt()
        val posY = (y + height - renderAmount).toInt()

        RenderUtils.bindBlockTexture()
        val color = fluid.fluid.getColor(fluid)
        GL11.glColor3ub((color shr 16 and 0xFF).toByte(), (color shr 8 and 0xFF).toByte(), (color and 0xFF).toByte())

        GlStateManager.enableBlend()
        var i = 0
        while (i < width) {
            var j = 0
            while (j < renderAmount) {
                val drawWidth = Math.min(width - i, 16.0).toInt()
                val drawHeight = Math.min(renderAmount - j, 16)

                val drawX = (x + i).toInt()
                val drawY = posY + j

                val minU = icon.minU.toDouble()
                val maxU = icon.maxU.toDouble()
                val minV = icon.minV.toDouble()
                val maxV = icon.maxV.toDouble()

                val tessellator = Tessellator.getInstance()
                val tes = tessellator.buffer
                tes.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX)
                tes.pos(drawX.toDouble(), (drawY + drawHeight).toDouble(), 0.0).tex(minU, minV + (maxV - minV) * drawHeight / 16f).endVertex()
                tes.pos((drawX + drawWidth).toDouble(), (drawY + drawHeight).toDouble(), 0.0).tex(minU + (maxU - minU) * drawWidth / 16f, minV + (maxV - minV) * drawHeight / 16f).endVertex()
                tes.pos((drawX + drawWidth).toDouble(), drawY.toDouble(), 0.0).tex(minU + (maxU - minU) * drawWidth / 16f, minV).endVertex()
                tes.pos(drawX.toDouble(), drawY.toDouble(), 0.0).tex(minU, minV).endVertex()
                tessellator.draw()
                j += 16
            }
            i += 16
        }
        GlStateManager.disableBlend()
    }

}