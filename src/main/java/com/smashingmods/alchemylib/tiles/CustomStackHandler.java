package com.smashingmods.alchemylib.tiles;

import com.smashingmods.alchemylib.utils.extensions.IItemHandlerUtils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.stream.IntStream;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class CustomStackHandler extends ItemStackHandler {

    public BaseTile tile;

    public CustomStackHandler(BaseTile tile, int size) {
        super(size);
        this.tile = tile;
    }

    @Override
    public void onContentsChanged(int slot) {
        super.onContentsChanged(slot);
        this.tile.setChanged();
    }

    public void clear() {
        IntStream.range(0, this.getSlots())
                .forEach(x -> setStackInSlot(x, ItemStack.EMPTY));
    }

    public void incrementSlot(int slot, int amount) {
        ItemStack temp = this.getStackInSlot(slot);
        if (temp.getCount() + amount <= temp.getMaxStackSize()) {
            temp.setCount(temp.getCount() + amount);
        }
        this.setStackInSlot(slot, temp);
    }

    public void setOrIncrement(int slot, ItemStack stackToSet) {
        if (!stackToSet.isEmpty()) {
            if (this.getStackInSlot(slot).isEmpty()) this.setStackInSlot(slot, stackToSet);
            else this.incrementSlot(slot, stackToSet.getCount());
        }
    }

    public void decrementSlot(int slot, int amount) {
        ItemStack temp = this.getStackInSlot(slot);
        if (temp.isEmpty()) return;
        if (temp.getCount() - amount < 0) return;
        temp.shrink(amount);
        if (temp.getCount() <= 0) this.setStackInSlot(slot, ItemStack.EMPTY);
        else this.setStackInSlot(slot, temp);
    }

    public boolean eject(Direction direction) {
        IItemHandler originHandler = this.tile.getCapability(ITEM_HANDLER_CAPABILITY, direction).orElse(null);
        IItemHandler targetHandler = this.tile.getLevel().getBlockEntity(tile.getBlockPos().offset(direction.getNormal()))
                .getCapability(ITEM_HANDLER_CAPABILITY, direction.getOpposite()).orElse(null);
        if (originHandler != null && targetHandler != null)
            return IItemHandlerUtils.tryInsertInto(originHandler, targetHandler);
        else return false;
    }
}
