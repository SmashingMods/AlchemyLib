package al132.alib.container;

import al132.alib.tiles.ABaseTile;
import al132.alib.tiles.GuiTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public abstract class ABaseContainer extends Container {

    public ABaseTile tile;
    private PlayerEntity player;
    private PlayerInventory playerInv;
    public final int SIZE;

    public ABaseContainer(ContainerType<?> type, int id, World world, BlockPos pos, PlayerInventory playerInv, PlayerEntity player, int slotCount) {
        super(type, id);
        this.tile = (ABaseTile) world.getTileEntity(pos);
        this.player = player;
        this.playerInv = playerInv;
        this.SIZE = slotCount;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }


    protected void addSlotArray(int startingIndex, int _x, int _y, int rows, int columns, IItemHandler handler) {
        int initialX = _x;
        int x = _x;
        int y = _y;

        int index = startingIndex;
        for (int row = 1; row <= rows; row++) {
            for (int column = 1; column <= columns; column++) {
                this.addSlot(new SlotItemHandler(handler, index, x, y));
                x += 18;
                index++;
            }
            x = initialX;
            y += 18;
        }
    }

    protected void addPlayerSlots() {
        for (int row = 0; row <= 2; row++) {
            for (int col = 0; col <= 8; col++){
                int x = 8 + col * 18;
                int y = row * 18 + ((GuiTile)tile).getHeight() - 82;
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row <= 8; row++){
            int x = 8 + row * 18;
            int y = ((GuiTile)tile).getHeight() - 24;
            this.addSlot(new Slot(playerInv, row, x, y));
        }
    }

/*
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < slotCount) {
                if (!this.mergeItemStack(itemstack1, slotCount, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, slotCount, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return itemstack;
    }*/

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);//[index];
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < SIZE) {
                if (!this.mergeItemStack(itemstack1, SIZE, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, SIZE, false)) return ItemStack.EMPTY;

            if (itemstack1.getCount() <= 0) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();
        }
        return itemstack;
    }

}
