package al132.alib.utils

import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by al132 on 1/2/2017.
 */
object Utils {

    fun oreExists(dictName: String): Boolean = OreDictionary.doesOreNameExist(dictName)

    fun firstOre(dictEntry: String): ItemStack = OreDictionary.getOres(dictEntry).firstOrNull()?: ItemStack.EMPTY

    fun toDictName(prefix: String, element: String) = prefix + element.first().toUpperCase() + element.substring(1)


    fun isHandlerEmpty(handler: IItemHandler): Boolean {
        return (0..handler.slots - 1).all { handler.getStackInSlot(it).isEmpty }
    }

    fun canStacksMerge(origin: ItemStack, target: ItemStack): Boolean {
        if (target.isEmpty || origin.isEmpty) return true
        else {
            return origin.item === target.item
                    && origin.count + target.count <= origin.maxStackSize
                    && origin.itemDamage == target.itemDamage
                    && origin.tagCompound === target.tagCompound
        }
    }

    fun areItemsEqualIgnoreMeta(stack1: ItemStack, stack2: ItemStack): Boolean {
        if (stack1.isEmpty && stack2.isEmpty) return true
        else if (!stack1.isEmpty && !stack2.isEmpty) return stack1.item === stack2.item
        else return false
    }

    fun getOres(name: String, quantity: Int): ImmutableList<ItemStack> {
        val temp = ImmutableList.Builder<ItemStack>()
        OreDictionary.getOres(name).forEach { x ->
            val y = x.copy()
            y.count = quantity
            temp.add(y)
        }
        return temp.build()
    }


    fun damage(inStack: ItemStack): ItemStack {
        val temp = inStack
        temp.itemDamage = inStack.itemDamage + 1

        if (temp.itemDamage >= temp.maxDamage) return ItemStack.EMPTY
        else return temp
    }
}