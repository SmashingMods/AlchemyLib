package al132.alib

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by al132 on 2/19/2017.
 */

@SideOnly(Side.CLIENT)
class ClientProxy : CommonProxy() {

    override fun preInit(e: FMLPreInitializationEvent) {
        super.preInit(e)
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)
    }

    override fun postInit(e: FMLPostInitializationEvent) {
        super.postInit(e)
    }
}