package al132.alib.container;

import al132.alib.tiles.*;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import static net.minecraftforge.energy.CapabilityEnergy.ENERGY;

public abstract class ABaseContainer extends AbstractContainerMenu {

    public ABaseTile tile;
    //private Player player;
    private Inventory playerInv;
    public final int SIZE;

    public ABaseContainer(MenuType<?> type, int id, Level world, BlockPos pos,
                          Inventory playerInv, int slotCount) {
        //MenuType<?> p_39229_, int p_39230_, Inventory p_39231_, Container p_39232_, int p_39233_
        super(type, id);
        this.tile = (ABaseTile) world.getBlockEntity(pos);
        //this.player = player;
        this.playerInv = playerInv;
        this.SIZE = slotCount;
        /*if (tile instanceof EnergyTile) trackInt(new DataSlot() {

            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                ((CustomEnergyStorage) tile.energy).setEnergy(value);
            }

        });
        if (tile instanceof FluidTile) trackInt(new DataSlot() {
            @Override
            public int get() {
                return ((FluidTile) tile).getFluidHandler().orElse(null).getFluidInTank(0).getAmount();
            }

            @Override
            public void set(int value) {
                FluidStack x = ((FluidTile) tile).getFluidHandler().orElse(null)
                        .getFluidInTank(0)
                        .copy();
                x.setAmount(value);
                ((FluidTank) ((FluidTile) tile).getFluidHandler().orElse(null)).setFluid(x);
            }
        });
*/

    }

    public void trackInt(DataSlot holder) {
        addDataSlot(new DataSlot() {
            private int lastKnown;

            @Override
            public int get() {
                return holder.get() & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = holder.get();
                holder.set((full & 0xffff0000) | (val & 0xffff));
            }

            @Override
            public boolean checkAndClearUpdateFlag() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });
        addDataSlot(new DataSlot() {
            private int lastKnown;

            @Override
            public int get() {
                return (holder.get() >> 16) & 0xffff;
            }

            @Override
            public void set(int val) {
                int full = holder.get();
                holder.set((full & 0x0000ffff) | ((val & 0xffff) << 16));
            }

            @Override
            public boolean checkAndClearUpdateFlag() {
                int i = this.get();
                boolean flag = i != this.lastKnown;
                this.lastKnown = i;
                return flag;
            }
        });
    }

    public int getEnergy() {
        return tile.getCapability(ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getFluid() {
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .map(x -> x.getFluidInTank(0).getAmount()).orElse(0);
    }

    public int getFluidCapacity() {
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
                .map(x -> x.getTankCapacity(0)).orElse(0);
    }

    public int getEnergyCapacity() {
        return tile.getCapability(ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

   /* @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
*/

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
            for (int col = 0; col <= 8; col++) {
                int x = 8 + col * 18;
                int y = row * 18 + ((GuiTile) tile).getHeight() - 82;
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, x, y));
            }
        }

        for (int row = 0; row <= 8; row++) {
            int x = 8 + row * 18;
            int y = ((GuiTile) tile).getHeight() - 24;
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
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);//[index];
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < SIZE) {
                if (!this.moveItemStackTo(itemstack1, SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, SIZE, false)) return ItemStack.EMPTY;

            if (itemstack1.getCount() <= 0) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return itemstack;
    }

}
