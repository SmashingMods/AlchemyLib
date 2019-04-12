package al132.alib.utils.extensions

import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack

fun Fluid.toStack(quantity: Int): FluidStack = FluidStack(this, quantity)
