package al132.alib.items

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack

abstract class ALMetaItem(name: String, tab: CreativeTabs) : ALItem(name,tab) {

    init {
        this.hasSubtypes = true
    }

    abstract override fun getUnlocalizedName(stack: ItemStack?): String
}