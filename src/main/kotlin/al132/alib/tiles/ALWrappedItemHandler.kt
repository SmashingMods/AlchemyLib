package al132.alib.tiles

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable

open class ALWrappedItemHandler(private val internalHandler: IItemHandlerModifiable) : IItemHandlerModifiable {

    override fun getSlots(): Int = internalHandler.slots

    override fun getStackInSlot(slot: Int) = internalHandler.getStackInSlot(slot)

    override fun setStackInSlot(slot: Int, stack: ItemStack) = internalHandler.setStackInSlot(slot, stack)

    override fun getSlotLimit(slot: Int) = internalHandler.getSlotLimit(slot)

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        return internalHandler.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
        return internalHandler.extractItem(slot, amount, simulate)
    }
}