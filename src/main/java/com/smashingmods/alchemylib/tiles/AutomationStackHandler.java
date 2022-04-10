package com.smashingmods.alchemylib.tiles;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class AutomationStackHandler implements IItemHandlerModifiable {

    private IItemHandlerModifiable internalHandler;

    public AutomationStackHandler(IItemHandlerModifiable handler) {
        this.internalHandler = handler;
    }

    public int getSlots() {
        return internalHandler.getSlots();
    }

    public ItemStack getStackInSlot(int slot) {
        return internalHandler.getStackInSlot(slot);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        internalHandler.setStackInSlot(slot, stack);
    }

    public int getSlotLimit(int slot) {
        return internalHandler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return internalHandler.insertItem(slot, stack, simulate);
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return internalHandler.extractItem(slot, amount, simulate);
    }
}