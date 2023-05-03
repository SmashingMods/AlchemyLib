package com.smashingmods.alchemylib.api.storage;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

/**
 * A wrapper around two {@link ProcessingSlotHandler} instances that can be used for
 * inserting and extracting items at the same time.
 *
 * <p>However, this implementation is aware of input and output slot restrictions and won't allow
 * the insertion of items into output slots as per {@link IItemHandler#insertItem(int, net.minecraft.world.item.ItemStack, boolean)}
 * and similarly won't allow the extraction in input slots.
 *
 * <p>TThe {@link SideMode} of each side can be configured through {@link #setSideMode(Direction, SideMode)}
 *
 * <p>If direct, unfiltered, access is required {@link #getDelegate()} can be used.
 *
 * <p>The amount of slots of the input and output handler should not change throughout runtime as the slot
 * IDs of the output handler are added ontop of the slot IDs of the input handler in order to combine both item handlers.
 */
@SuppressWarnings("unused")
public class SidedProcessingSlotWrapper {
    public static final int LEGACY_SIDES_CONFIGURATION = SideMode.PULL.ordinal() << (Direction.UP.ordinal() * 2)
            | SideMode.PULL.ordinal() << (Direction.WEST.ordinal() * 2)
            | SideMode.PUSH.ordinal() << (Direction.DOWN.ordinal() * 2)
            | SideMode.PUSH.ordinal() << (Direction.EAST.ordinal() * 2);

    private final ProcessingSlotHandler inputHandler;
    private final ProcessingSlotHandler outputHandler;
    private final SideMode[] sideModes = new SideMode[7]; // 4 cardinal directions + up/down + unspecified side = 7 sides total
    @SuppressWarnings("unchecked") // Java does not allow creating arrays with generics for some ungodly reason
    private final LazyOptional<IItemHandler>[] views = new LazyOptional[7];

    private class SidedItemHandlerView implements IItemHandlerModifiable {
        private final Direction side;

        public SidedItemHandlerView(Direction side) {
            this.side = side;
        }

        @Override
        public int getSlots() {
            return inputHandler.getSlots() + outputHandler.getSlots();
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot < inputHandler.getSlots()) {
                return inputHandler.getStackInSlot(slot);
            } else {
                return outputHandler.getStackInSlot(slot - inputHandler.getSlots());
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!getSideMode(side).isPullEnabled() || slot >= inputHandler.getSlots()) {
                return stack;
            }
            return inputHandler.insertItem(slot, stack, simulate);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!getSideMode(side).isPushEnabled() || slot < inputHandler.getSlots()) {
                return ItemStack.EMPTY;
            }
            return outputHandler.extractItem(slot- inputHandler.getSlots(), amount, simulate);
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
        return getViewLazily(side).orElse(null);
    }

    public LazyOptional<IItemHandler> getViewLazily(@Nullable Direction side) {
        LazyOptional<IItemHandler> view = views[side == null ? 6 : side.ordinal()];
        if (view == null) {
            view = LazyOptional.of(() -> new SidedItemHandlerView(side));
            views[side == null ? 6 : side.ordinal()] = view;
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

    public void invalidate() {
        for (LazyOptional<IItemHandler> view : views) {
            if (view != null) {
                view.invalidate();
            }
        }
    }

    /**
     * Serialise and pack side modes into an 16-bit integer (short).
     *
     * @return The packed representation of the current state of the sideModes array.
     */
    public short sideModesToShort() {
        int value = 0;
        for (int i = 0; i < sideModes.length; i++) {
            value |= sideModes[i].ordinal() << (i * 2);
        }
        return (short) value;
    }

    /**
     * Unpack and apply a packed 16-bit side modes array.
     *
     * @param value The packed value as obtained from {@link #sideModesToShort()}.
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
