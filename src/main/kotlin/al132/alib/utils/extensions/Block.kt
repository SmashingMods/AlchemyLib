package al132.alib.utils.extensions

import net.minecraft.block.Block
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient

/**
 * Created by al132 on 4/27/2017.
 */

fun Block.toStack(quantity: Int = 1, meta: Int = 0) = ItemStack(this,quantity,meta)

fun Block.toIngredient(quantity: Int = 1, meta: Int = 0): Ingredient = Ingredient.fromStacks(this.toStack(quantity, meta))