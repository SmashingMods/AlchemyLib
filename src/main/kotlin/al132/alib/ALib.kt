package al132.alib

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Mod(modid = Reference.MODID,
        name = Reference.NAME,
        version = Reference.VERSION,
        dependencies = Reference.DEPENDENCIES,
        modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object ALib {

    @SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.SERVER)
    lateinit var PROXY: CommonProxy

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        PROXY.preInit(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        PROXY.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        PROXY.postInit(e)
    }
}