package al132.alib

import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.items.CapabilityItemHandler

/**
 * Creatcd wed by al132 on 2/26/2017.
 */


object Reference {

    const val MODID = "alib"
    const val NAME = "A Lib"
    const val VERSION = "1.0.8"
    const val CLIENT = "al132.alib.ClientProxy"
    const val SERVER = "al132.alib.CommonProxy"
    const val DEPENDENCIES = "required-after:forgelin"

    val ENERGY_CAP = CapabilityEnergy.ENERGY
    val ITEM_CAP = CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
    val FLUID_CAP = CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY

}