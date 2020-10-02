package al132.alib.client;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class CapabilityFluidDisplayWrapper extends CapabilityDisplayWrapper {


    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    public Supplier<IFluidHandler> fluid;


    public CapabilityFluidDisplayWrapper(int x, int y, int width, int height, Supplier<IFluidHandler> fluid) {
        super(x, y, width, height);
        this.fluid = fluid;
    }

    @Override
    public int getStored() {
        return fluid.get().getFluidInTank(0).getAmount();
        // else return -1;
    }

    @Override
    public int getCapacity() {
        return fluid.get().getTankCapacity(0);
    }


    @Override
    public String toString() {
        FluidStack stack = fluid.get().getFluidInTank(0);
        String fluidName = "";
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getCapacity());
        if (!stack.isEmpty() && stack.getAmount() > 0) {
            fluidName = I18n.format(stack.getFluid().getAttributes().getTranslationKey());
        }
        String out = stored + "/" + capacity + " mb " + fluidName;
        return out;
    }
}