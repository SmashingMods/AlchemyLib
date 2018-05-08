package al132.alib.utils.extensions

import com.google.common.collect.ImmutableList
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by al132 on 4/23/2017.
 */


fun <E> List<E>.toImmutable(): ImmutableList<E> = ImmutableList.Builder<E>().addAll(this).build()

fun List<ItemStack>.containsStack(stack: ItemStack): Boolean = any { ItemStack.areItemStacksEqual(it, stack) }

fun List<ItemStack>.containsItem(stack: ItemStack): Boolean = any { ItemStack.areItemsEqual(it, stack) }

fun List<ItemStack>.copy(): List<ItemStack> {
    val temp = ArrayList<ItemStack>()
    this.forEach { temp.add(it.copy()) }
    return temp
}