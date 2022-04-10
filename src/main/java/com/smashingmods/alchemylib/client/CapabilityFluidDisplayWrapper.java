package com.smashingmods.alchemylib.client;

import com.smashingmods.alchemylib.container.BaseContainer;
import com.smashingmods.alchemylib.tiles.BaseTile;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityFluidDisplayWrapper extends CapabilityDisplayWrapper {


    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    private BaseContainer container = null;
    private BaseTile tile = null;

    public CapabilityFluidDisplayWrapper(int x, int y, int width, int height, BaseContainer container) {
        super(x, y, width, height);
        this.container = container;
    }

    public CapabilityFluidDisplayWrapper(int x, int y, int width, int height, BaseTile tile) {
        super(x, y, width, height);
        this.tile = tile;
    }

    public IFluidHandler getHandler() {
        if (container != null) {
            return container.tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                    .orElse(null);
        } else return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);


    }

    @Override
    public int getStored() {
        return this.getHandler().getFluidInTank(0).getAmount();
        // else return -1;
    }

    @Override
    public int getCapacity() {
        return getHandler().getTankCapacity(0);
    }


    @Override
    public String toString() {
        FluidStack stack = getHandler().getFluidInTank(0);
        String fluidName = "";
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getCapacity());
        if (!stack.isEmpty() && stack.getAmount() > 0) {
            fluidName = I18n.get(stack.getFluid().getAttributes().getTranslationKey());
        }
        String out = stored + "/" + capacity + " mb " + fluidName;
        return out;
    }
}