package com.smashingmods.alchemylib.tiles;

import net.minecraftforge.energy.IEnergyStorage;

public interface EnergyTile {

    //LazyOptional<IEnergyStorage> getEnergyHolder();

    IEnergyStorage initEnergy();

    IEnergyStorage getEnergy();
}