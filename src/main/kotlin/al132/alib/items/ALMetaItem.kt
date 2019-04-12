package al132.alib.items

import net.minecraft.creativetab.CreativeTabs

abstract class ALMetaItem(name: String, tab: CreativeTabs) : ALItem(name,tab) {

    init {
        this.hasSubtypes = true
    }
}