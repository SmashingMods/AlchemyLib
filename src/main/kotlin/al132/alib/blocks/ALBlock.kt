package al132.alib.blocks


import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

//extend this class
abstract class ALBlock(name: String,
                       creativeTab: CreativeTabs,
                       material: Material = Material.ROCK,
                       hardness: Float = 3.0F)
    : Block(material), IModelBlock {

    init {
        translationKey = name
        setRegistryName(name)
        this.setHardness(hardness)
        this.creativeTab = creativeTab
    }

    open fun registerBlock(event: RegistryEvent.Register<Block>){
        event.registry.register(this)
    }

    open fun registerItemBlock(event: RegistryEvent.Register<Item>){
        event.registry.register(ItemBlock(this).setRegistryName(this.registryName))
    }

    @SideOnly(Side.CLIENT)
    override fun registerModel() {
        ModelLoader.setCustomModelResourceLocation(
                Item.getItemFromBlock(this), 0, ModelResourceLocation(registryName!!, "inventory"))
    }
}