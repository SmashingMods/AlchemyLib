package al132.alib.items


import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

open class ALItem(name: String,
                  tab: CreativeTabs) : Item() {

    init {
        setRegistryName(name)
        unlocalizedName = this.registryName!!.toString()
        creativeTab = tab
    }

    open fun registerItem(event: RegistryEvent.Register<Item>){
        event.registry.register(this)
    }

    @SideOnly(Side.CLIENT)
    open fun registerModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!, "inventory"))
    }
}
