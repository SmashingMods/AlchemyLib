package al132.alib.tiles;

import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyTile {

    //LazyOptional<IEnergyStorage> getEnergyHolder();

    IEnergyStorage initEnergy();

    IEnergyStorage getEnergy();
}