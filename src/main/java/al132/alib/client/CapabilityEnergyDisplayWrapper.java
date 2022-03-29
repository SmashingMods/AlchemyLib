package al132.alib.client;

import al132.alib.container.ABaseContainer;
import al132.alib.tiles.ABaseTile;
import al132.alib.tiles.EnergyTile;
import com.google.common.collect.Lists;
import net.minecraftforge.energy.IEnergyStorage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class CapabilityEnergyDisplayWrapper extends CapabilityDisplayWrapper {

    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    //public Supplier<IEnergyStorage> energy;
    private ABaseContainer container = null;
    private ABaseTile tile = null;

    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height, ABaseContainer container) {
        super(x, y, width, height);
        this.container = container;
    }

    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height, ABaseTile tile) {
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