package com.smashingmods.alchemylib.api.blockentity.container;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.common.network.BlockEntityPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * This abstract class extends {@link AbstractContainerMenu} by adding overrides for mod support. It also provides
 * a number of helper methods for adding slots to the container menu.
 * 
 */
@SuppressWarnings("unused")
public abstract class AbstractProcessingMenu extends AbstractContainerMenu {

    private final AbstractProcessingBlockEntity blockEntity;
    private final Level level;
    private final int inputSlots;
    private final int outputSlots;

    protected AbstractProcessingMenu(MenuType<?> pMenuType, int pContainerId, Inventory pInventory, BlockEntity pBlockEntity, int pInputSlots, int pOutputSlots) {
        super(pMenuType, pContainerId);

        this.inputSlots = pInputSlots;
        this.outputSlots = pOutputSlots;
        this.blockEntity = ((AbstractProcessingBlockEntity) pBlockEntity);
        this.level = pInventory.player.level();

        addPlayerInventorySlots(pInventory);
    }

    /**
     * This method is called when changes are made to the container for this menu. Since changes are processed and
     * stored on the server side, the container needs to notify the client side that the changes were made to
     * keep data in sync.
     *
     * <p>This method sends a packet from the server to the client with the parent BlockEntity's update tag.</p>
     *
     * @see AbstractProcessingBlockEntity#getUpdateTag()
     */
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (level != null && !level.isClientSide()) {
            AlchemyLib.getPacketHandler().sendToTrackingChunk(new BlockEntityPacket(getBlockEntity().getBlockPos(), getBlockEntity().getUpdateTag()), getLevel(), getBlockEntity().getBlockPos());
        }
    }

    /**
     * Overrides {@link AbstractContainerMenu#quickMoveStack(Player, int)} to provide support for more slots added
     * by AlchemyLib machines.
     */
    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        int blockEntitySlots = inputSlots + outputSlots;
        Slot sourceSlot = slots.get(pIndex);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyStack = sourceStack.copy();

        if (pIndex < 36) {
            if (!moveItemStackTo(sourceStack, 36, 36 + inputSlots, false)) {
                return ItemStack.EMPTY;
            }
        } else if (pIndex < 36 + inputSlots) {
            if (!moveItemStackTo(sourceStack, 0, 36, false))  {
                return ItemStack.EMPTY;
            }
        } else if (pIndex >= 36 + inputSlots && pIndex < 36 + blockEntitySlots) {
            if (!moveItemStackTo(sourceStack, 0, 36, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(pPlayer, sourceStack);
        return copyStack;
    }


    /**
     * This interface can be used to represent a large range of objects including implementers of {@link Slot}.
     *
     * @param <T> Represents a {@link Container}, {@link com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler ProcessingSlotHandler}, or any other object that
     *           holds ItemStacks or has an inventory.
     */
    @FunctionalInterface
    public interface SlotType<T> {
        Slot apply(T pContainer, int pSlotIndex, int pX, int pY);
    }

    /**
     * Overload for adding a single slot with exactly 1 row and 1 column.
     *
     * @param pSlotType {@link SlotType}
     */
    protected <T> void addSlots(SlotType<T> pSlotType, T pContainer, int pXOrigin, int pYOrigin) {
        addSlots(pSlotType, pContainer, 1, 1, 0, 1, pXOrigin, pYOrigin);
    }

    /**
     * Overload for adding a single slot where the total slots of the handler might be higher than 1. This method is
     * useful for displaying slots for the same handler/container in different locations on a screen.
     *
     * @param pSlotType {@link SlotType}
     */
    protected <T> void addSlots(SlotType<T> pSlotType, T pContainer, int pStartIndex, int pTotalSlots, int pXOrigin, int pYOrigin) {
        addSlots(pSlotType, pContainer, 1, 1, pStartIndex, pTotalSlots, pXOrigin, pYOrigin);
    }

    /**
     * This method can be used to add slots to a container menu.
     *
     * @param pSlotType {@link SlotType}
     * @param pContainer Container represents an object that can have items. Should either implement {@link Container}
     *                   or pass {@link com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler ProcessingSlotHandler}.
     * @param pRows Integer of the rows of slots to be added.
     * @param pColumns Integer of the columns of slots to be added.
     * @param pStartIndex Index of the container to use to start from. For example, if you have an inventory with 36 slots
     *                    and you want to display a row of 9 slots starting at index 9, set this to 9 and set total slots to 36.
     * @param pTotalSlots Total number of slots to add.
     * @param pXOrigin Anchor value of the X position where slots are drawn from.
     * @param pYOrigin Anchor value of the Y position where slots are drawn from.
     * @param <T> See pContainer.
     */
    protected <T> void addSlots(SlotType<T> pSlotType, T pContainer, int pRows, int pColumns, int pStartIndex, int pTotalSlots, int pXOrigin, int pYOrigin) {

        for (int row = 0; row < pRows; row++) {
            for (int column = 0; column < pColumns; column++) {
                int slotIndex = column + row * pColumns + pStartIndex;
                int x = pXOrigin + column * 18;
                int y = pYOrigin + row * 18;

                if (slotIndex < pStartIndex + pTotalSlots) {
                    this.addSlot(pSlotType.apply(pContainer, slotIndex, x, y));
                }
            }
        }
    }

    /**
     * This method adds the player's inventory slots to the menu to make sure their items are accessible and drawn to
     * the screen.
     *
     * @param pInventory Inventory for the player accessing the container menu.
     */
    public void addPlayerInventorySlots(Inventory pInventory) {
        // player main inventory
        addSlots(Slot::new, pInventory, 3, 9, 9, 27,12, 76);
        // player hotbar
        addSlots(Slot::new, pInventory, 1, 9, 0, 9,12, 134);
    }

    /**
     * The screen for this menu doesn't have direct access to the block entity of the container, so this is necessary
     * for getting a reference.
     * @return {@link AbstractProcessingBlockEntity} A reference to the menu's block entity.
     */
    public AbstractProcessingBlockEntity getBlockEntity() {
        return blockEntity;
    }

    /**
     * The screen for this menu doesn't have direct access to the level of the container, so this is necessary for getting
     *  a reference.
     *
     * @return Level for this menu container and block entity.
     */
    public Level getLevel() {
        return level;
    }
}
