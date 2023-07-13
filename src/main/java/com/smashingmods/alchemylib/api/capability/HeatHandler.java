package com.smashingmods.alchemylib.api.capability;

import io.netty.util.internal.UnstableApi;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;

@SuppressWarnings("unused")
@UnstableApi
public class HeatHandler implements HeatCapability {

    private int heat;
    private final int maxHeat;

    public HeatHandler(int pMax) {
        this.maxHeat = pMax;
    }

    @Override
    public int getHeat() {
        return heat;
    }

    @Override
    public void setHeat(int pHeat) {
        this.heat = pHeat;
    }

    @Override
    public void increment(int pHeat) {
        if (heat + pHeat <= maxHeat) {
            heat += pHeat;
        } else {
            heat = maxHeat;
        }
    }

    @Override
    public void decrement(int pHeat) {
        if (heat - pHeat >= 0) {
            heat -= pHeat;
        } else {
            heat = 0;
        }
    }

    @Override
    public int getMaxHeat() {
        return maxHeat;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("heat", heat);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag pTag) {
        setHeat(pTag.getInt("heat"));
    }

    @Override
    public Component getComponent() {
        return MutableComponent.create(new LiteralContents(String.format("%s H", heat)));
    }

    @Override
    public String toString() {
        return String.format("[%s heat, %s max heat]", heat, maxHeat);
    }
}
