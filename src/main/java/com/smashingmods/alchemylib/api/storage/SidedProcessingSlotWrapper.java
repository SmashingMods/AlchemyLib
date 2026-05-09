package com.smashingmods.alchemylib.api.storage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

/**
 * Wraps two {@link ProcessingSlotHandler} instances with side-aware insert/extract restrictions.
 *
 * <p>The {@link SideMode} per side is configured via {@link #setSideMode(Direction, SideMode)}.
 * Output slots reject inserts; input slots reject extracts.
 */
@SuppressWarnings({"unused", "ConstantConditions"})
public class SidedProcessingSlotWrapper {
    public static final int LEGACY_SIDES_CONFIGURATION = SideMode.PULL.ordinal() << (Direction.UP.ordinal() * 2)
            | SideMode.PULL.ordinal() << (Direction.WEST.ordinal() * 2)
            | SideMode.PUSH.ordinal() << (Direction.DOWN.ordinal() * 2)
            | SideMode.PUSH.ordinal() << (Direction.EAST.ordinal() * 2);

    private final ProcessingSlotHandler inputHandler;
    private final ProcessingSlotHandler outputHandler;
    private final SideMode[] sideModes = new SideMode[7]; // 6 directions + null
    private final IItemHandler[] views = new IItemHandler[7];

    private class SidedItemHandlerView implements IItemHandlerModifiable {
        private final Direction side;

        public SidedItemHandlerView(@Nullable Direction side) {
            this.side = side;
        }

        @Override
        public int getSlots() {
            return inputHandler.getSlots() + outputHandler.getSlots();
        }

        @Override
        @Nonnull
        public ItemStack getStackInSlot(int slot) {
            if (slot < inputHandler.getSlots()) {
                return inputHandler.getStackInSlot(slot);
            } else {
                return outputHandler.getStackInSlot(slot - inputHandler.getSlots());
            }
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!getSideMode(side).isPullEnabled() || slot >= inputHandler.getSlots()) {
                return stack;
            }
            return inputHandler.insertItem(slot, stack, simulate);
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!getSideMode(side).isPushEnabled() || slot < inputHandler.getSlots()) {
                return ItemStack.EMPTY;
            }
            return outputHandler.extractItem(slot - inputHandler.getSlots(), amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < inputHandler.getSlots()) {
                return inputHandler.getSlotLimit(slot);
            } else {
                return outputHandler.getSlotLimit(slot - inputHandler.getSlots());
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot < inputHandler.getSlots()) {
                return inputHandler.isItemValid(slot, stack);
            } else {
                return outputHandler.isItemValid(slot - inputHandler.getSlots(), stack);
            }
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if (slot < inputHandler.getSlots()) {
                inputHandler.setStackInSlot(slot, stack);
            } else {
                outputHandler.setStackInSlot(slot - inputHandler.getSlots(), stack);
            }
        }
    }

    public SidedProcessingSlotWrapper(ProcessingSlotHandler input, ProcessingSlotHandler output) {
        for (int i = 0; i < 7; i++) {
            this.sideModes[i] = SideMode.ENABLED;
        }
        this.inputHandler = input;
        this.outputHandler = output;
    }

    public IItemHandler getView(@Nullable Direction side) {
        int idx = side == null ? 6 : side.ordinal();
        IItemHandler view = views[idx];
        if (view == null) {
            view = new SidedItemHandlerView(side);
            views[idx] = view;
        }
        return view;
    }

    public void setSideMode(@Nullable Direction side, SideMode mode) {
        sideModes[side == null ? 6 : side.ordinal()] = mode;
    }

    public SideMode getSideMode(@Nullable Direction side) {
        return sideModes[side == null ? 6 : side.ordinal()];
    }

    public ProcessingSlotHandler getInputHandler() {
        return inputHandler;
    }

    public ProcessingSlotHandler getOutputHandler() {
        return outputHandler;
    }

    /**
     * Pack side modes into a 16-bit short.
     */
    public short sideModesToShort() {
        int value = 0;
        for (int i = 0; i < sideModes.length; i++) {
            value |= sideModes[i].ordinal() << (i * 2);
        }
        return (short) value;
    }

    /**
     * Unpack and apply a packed short of side modes.
     */
    public void setSideModesFromShort(int value) {
        for (int i = 0; i < sideModes.length; i++) {
            sideModes[i] = SideMode.getFromOrdinal(value & 0b11);
            value >>= 2;
        }
        if (value != 0) {
            throw new IllegalArgumentException("Did not apply cleanly: Rest " + value);
        }
    }
}
