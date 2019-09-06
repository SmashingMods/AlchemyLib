package al132.alib.client;

import com.google.common.collect.Lists;
import net.minecraftforge.energy.IEnergyStorage;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class CapabilityEnergyDisplayWrapper extends CapabilityDisplayWrapper {

    public static NumberFormat numFormat = NumberFormat.getInstance(Locale.US);

    public Supplier<IEnergyStorage> energy;


    public CapabilityEnergyDisplayWrapper(int x, int y, int width, int height,Supplier<IEnergyStorage> energy) {
        super(x, y, width, height);
        this.energy = energy;
    }

    @Override
    public int getStored() {
        return energy.get().getEnergyStored();
       // else return -1;
    }

    @Override
    public int getCapacity() {
        return energy.get().getMaxEnergyStored();
        //if (energy.isPresent()) return energy.orElse(null).getMaxEnergyStored();
        //else return -1;//.get().getMaxEnergyStored();
    }

    @Override
    public List<String> toStringList() {
        String stored = numFormat.format(getStored());
        String capacity = numFormat.format(getCapacity());
        String tooltip = stored + "/" + capacity + " FE";
        return Lists.newArrayList(tooltip);
    }
}