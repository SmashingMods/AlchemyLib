package al132.alib.blocks

import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

interface IModelBlock {

    @SideOnly(Side.CLIENT)
    fun registerModel()
}