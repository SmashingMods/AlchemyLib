package com.smashingmods.alchemylib.api.capability;

import io.netty.util.internal.UnstableApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.capabilities.AutoRegisterCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;

@SuppressWarnings("unused")
@AutoRegisterCapability
@UnstableApi
public interface HeatCapability extends INBTSerializable<CompoundTag> {

    int getHeat();

    void setHeat(int pHeat);

    void increment(int pHeat);

    void decrement(int pHeat);

    int getMaxHeat();

    Component getComponent();

    String toString();
}
