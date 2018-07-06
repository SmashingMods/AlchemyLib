package al132.alib.tiles

import al132.alib.Reference.ITEM_CAP
import al132.alib.utils.Utils
import al132.alib.utils.extensions.get
import al132.alib.utils.extensions.tryInsertInto
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.items.ItemStackHandler

open class ALTileStackHandler(size: Int, var tile: ALTile) : ItemStackHandler() {


    init {
        this.setSize(size)
    }

    fun damageSlot(slot: Int) = this.setStackInSlot(slot, Utils.damage(this.getStackInSlot(slot)))

    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        this.tile.markDirty()
    }

    fun clear() = (0 until this.slots).forEach { this.setStackInSlot(it, ItemStack.EMPTY) }

    fun incrementSlot(slot: Int, amountToAdd: Int) {
        val temp = this[slot]
        if (temp.count + amountToAdd <= temp.maxStackSize) {
            temp.count = temp.count + amountToAdd
        }
        this.setStackInSlot(slot, temp)
    }

    fun setOrIncrement(slot: Int, stackToSet: ItemStack) {
        if(!stackToSet.isEmpty) {
            if (this[slot].isEmpty) this.setStackInSlot(slot, stackToSet)
            else this.incrementSlot(slot, stackToSet.count)
        }
    }

    fun decrementSlot(slot: Int, amount: Int) {
        val temp = this[slot]
        if (temp.isEmpty) return
        if (temp.count - amount < 0) return

        temp.shrink(amount)
        if (temp.count <= 0) this.setStackInSlot(slot, ItemStack.EMPTY)
        else this.setStackInSlot(slot, temp)
    }

    fun eject(direction: EnumFacing): Boolean {
        val originHandler = this.tile.getCapability(ITEM_CAP, direction)
        val targetHandler = this.tile.world.getTileEntity(tile.pos.offset(direction))
                ?.getCapability(ITEM_CAP, direction.opposite)

        if (originHandler != null && targetHandler != null) return originHandler.tryInsertInto(targetHandler)
        else return false
    }
}