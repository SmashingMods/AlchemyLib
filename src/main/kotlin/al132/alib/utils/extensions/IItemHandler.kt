package al132.alib.utils.extensions

import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import java.util.*

/**
 * Created by al132 on 4/18/2017.
 */

operator fun IItemHandler.get(index: Int): ItemStack = this.getStackInSlot(index)


fun IItemHandler.tryInsertInto(otherHandler: IItemHandler): Boolean {
    for (i in 0 until otherHandler.slots) {
        for (j in 0 until this.slots) {
            if (!this.getStackInSlot(j).isEmpty) {
                val stackSize = this.countSlot(j)
                if (otherHandler.insertItem(i, this.extractItem(j, stackSize, true), true).isEmpty) {
                    otherHandler.insertItem(i, this.extractItem(j, stackSize, false), false)
                    return true
                }
            }
        }
    }
    return false
}

fun IItemHandler.toStackList(): ArrayList<ItemStack> {
    val temp = ArrayList<ItemStack>()
    (0 until this.slots).forEach {
        val stack: ItemStack = this.getStackInSlot(it)
        if (!stack.isEmpty) temp.add(stack)
        else temp.add(ItemStack.EMPTY)
    }
    return temp
}

fun IItemHandler.countSlot(index: Int) = this.getStackInSlot(index).count