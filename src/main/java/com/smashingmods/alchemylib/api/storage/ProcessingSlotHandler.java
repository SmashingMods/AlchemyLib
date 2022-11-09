package com.smashingmods.alchemylib.api.storage;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

/**
 * This class is a wrapper for {@link ItemStackHandler} that provides helper methods for
 */
@SuppressWarnings("unused")
public class ProcessingSlotHandler extends ItemStackHandler {

    public ProcessingSlotHandler(int pSize) {
        super(pSize);
    }

    /**
     * Increments the count of the ItemStack in the given slot of this handler by the ammount.
     */
    public void incrementSlot(int pSlot, int pAmount) {
        ItemStack temp = this.getStackInSlot(pSlot);

        if (temp.getCount() + pAmount <= temp.getMaxStackSize()) {
            temp.setCount(temp.getCount() + pAmount);
        }

        this.setStackInSlot(pSlot, temp);
    }

    /**
     * Sets an ItemStack into this handler's slot. If the slot already has that
     * ItemStack, it increments it by the ItemStack's count. If that slot isn't empty,
     * this method does nothing.
     *
     * @param pSlot Integer value representing this item handler's slot.
     * @param pItemStack {@link ItemStack}
     */
    public void setOrIncrement(int pSlot, ItemStack pItemStack) {
        if (!pItemStack.isEmpty()) {
            if (getStackInSlot(pSlot).isEmpty()) {
                setStackInSlot(pSlot, pItemStack);
            } else {
                incrementSlot(pSlot, pItemStack.getCount());
            }
        }
    }

    /**
     * This method is used to decrement the count of the ItemStack in this handler's slot.
     * If it decrements to 0, the slot's ItemStack is set to EMPTY.
     *
     * @param pSlot Integer value representing this item handler's slot.
     * @param pAmount Integer value for how much to decrease the size of the ItemStack in the slot.
     */
    public void decrementSlot(int pSlot, int pAmount) {
        ItemStack temp = this.getStackInSlot(pSlot);

        if (temp.isEmpty()) return;
        if (temp.getCount() - pAmount < 0) return;

        temp.shrink(pAmount);
        if (temp.getCount() <= 0) {
            this.setStackInSlot(pSlot, ItemStack.EMPTY);
        } else {
            this.setStackInSlot(pSlot, temp);
        }
    }

    /**
     * Empties the ItemStacks held by this handler into the Inventory passed to the method.
     * This method doesn't check to see if the Inventory can hold more items at any step
     * of the process. Consumers of this method will need to verify that the Inventory has
     * space for all ItemStacks held by this handler.
     *
     * @param pInventory {@link Inventory}
     */
    public void emptyToInventory(Inventory pInventory) {
        for (int i = 0; i < this.stacks.size(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                ItemHandlerHelper.insertItemStacked(new PlayerInvWrapper(pInventory), getStackInSlot(i), false);
                setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    /**
     * Tests to see if the stacks held by this handler are all empty.
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return stacks.stream().allMatch(ItemStack::isEmpty);
    }

    /**
     * @return NonNullList of ItemStack contained by this handler.
     */
    public NonNullList<ItemStack> getStacks() {
        return this.stacks;
    }
}
