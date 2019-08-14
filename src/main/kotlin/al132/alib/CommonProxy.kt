package al132.alib

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

/**
 * Created by al132 on 2/19/2017.
 */

open class CommonProxy {

    open fun preInit(e: FMLPreInitializationEvent) {
        ConfigHandler.init(e.suggestedConfigurationFile)
    }

    open fun init(e: FMLInitializationEvent) {
        if (ConfigHandler.announcement?.isNotEmpty() == true || ConfigHandler.url?.isNotEmpty() == true) {
            MinecraftForge.EVENT_BUS.register(EventHandler())
        }
    }

    open fun postInit(e: FMLPostInitializationEvent) {
    }
}
