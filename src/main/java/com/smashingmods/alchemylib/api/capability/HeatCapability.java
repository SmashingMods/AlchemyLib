package com.smashingmods.alchemylib.api.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;

@SuppressWarnings("unused")
@AutoRegisterCapability
public interface HeatCapability extends INBTSerializable<CompoundTag> {

    int getHeat();

    void setHeat(int pHeat);

    void increment(int pHeat);

    void decrement(int pHeat);

    int getMaxHeat();

    Component getComponent();

    String toString();
}
