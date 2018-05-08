package al132.alib.utils.extensions

import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by al132 on 4/27/2017.
 */

fun Item.toStack(quantity: Int = 1, meta: Int = 0) = ItemStack(this,quantity,meta)