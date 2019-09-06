package al132.alib.tiles;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

//TODO
public interface FluidTile {
    LazyOptional<IFluidHandler> getFluidHandler();
}
