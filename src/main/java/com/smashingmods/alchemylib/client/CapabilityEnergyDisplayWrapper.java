package com.smashingmods.alchemylib.client;

import com.smashingmods.alchemylib.container.BaseContainer;
import com.smashingmods.alchemylib.tiles.BaseTile;
import com.smashingmods.alchemylib.tiles.EnergyTile;

import java.text.NumberFormat;
import java.util.Locale;

public class CapabilityEnergyDisplayWrapper extends CapabilityDisplayWrapper {

    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    //public Supplier<IEnergyStorage> energy;
    private BaseContainer container = null;
    private BaseTile tile = null;

    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height, BaseContainer container) {
        super(x, y, width, height);
        this.container = container;
    }

    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height, BaseTile tile) {
        super(x, y, width, height);
        this.tile = tile;

    }

    @Override
    public int getStored() {
        if (container != null) return container.getEnergy();//energy.get().getEnergyStored();
        else if (tile != null) return ((EnergyTile) tile).getEnergy().getEnergyStored();
        else return -1;
        // else return -1;
    }

    @Override
    public int getCapacity() {
        if (container != null) return container.getEnergyCapacity();
        else if (tile != null) return ((EnergyTile) tile).getEnergy().getMaxEnergyStored();
        else return -1;


        //energy.get().getMaxEnergyStored();
        //if (energy.isPresent()) return energy.orElse(null).getMaxEnergyStored();
        //else return -1;//.get().getMaxEnergyStored();
    }

    @Override
    public String toString() {
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getCapacity());
        String tooltip = stored + "/" + capacity + " FE";
        return tooltip;
    }
}