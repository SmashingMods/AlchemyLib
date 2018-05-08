package al132.alib.tiles

import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by al132 on 1/9/2017.
 */
interface IGuiTile {

    val guiHeight: Int
        get() = 222

    val guiWidth: Int
        get() = 174
}

interface IFluidTile {
    val fluidTanks: FluidHandlerConcatenate?
        get() = null
}

interface IEnergyTile

interface IItemTile