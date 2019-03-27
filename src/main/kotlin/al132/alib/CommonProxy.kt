package al132.alib

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by al132 on 2/19/2017.
 */

open class CommonProxy {

    open fun preInit(e: FMLPreInitializationEvent) {
    }

    open fun init(e: FMLInitializationEvent) {
        //MinecraftForge.EVENT_BUS.register(EventHandler())
    }

    open fun postInit(e: FMLPostInitializationEvent) {
    }
}
