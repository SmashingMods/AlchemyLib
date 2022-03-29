package al132.alib.tiles;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class CustomFluidTank extends FluidTank {
    public CustomFluidTank(int capacity, FluidStack stack) {
        super(capacity);
        this.fill(stack, FluidAction.EXECUTE);
    }

    public void set(FluidStack stack) {
        this.drain(this.capacity, FluidAction.EXECUTE);
        this.fill(stack, FluidAction.EXECUTE);
    }
}
