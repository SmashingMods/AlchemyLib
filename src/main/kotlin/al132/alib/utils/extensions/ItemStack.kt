package al132.alib.utils.extensions

import al132.alib.utils.Utils
import net.minecraft.item.ItemStack

/**
 * Created by al132 on 4/14/2017.
 */


fun ItemStack.equalsIgnoreMeta(stack2: ItemStack) = Utils.areItemsEqualIgnoreMeta(this, stack2)

fun ItemStack.areItemStacksEqual(other: ItemStack) = ItemStack.areItemStacksEqual(this, other)

fun ItemStack.areItemsEqual(other: ItemStack) = ItemStack.areItemsEqual(this, other)