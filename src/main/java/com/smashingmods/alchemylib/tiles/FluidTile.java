package com.smashingmods.alchemylib.tiles;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

//TODO
public interface FluidTile {
    LazyOptional<IFluidHandler> getFluidHandler();
}
