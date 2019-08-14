package al132.alib

import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.relauncher.Side


class EventHandler {

    var called = false

    @SubscribeEvent
    fun playerTickEvent(e: TickEvent.PlayerTickEvent) {
        if ((e.player.world.totalWorldTime % ConfigHandler.tickInterval!!) == 0L) {
            if (e.side == Side.CLIENT) {
                if (e.player.world.isRemote && !called) {
                    if (ConfigHandler.announcement?.isNotEmpty() == true) {
                        e.player.sendMessage(TextComponentString(ConfigHandler.announcement!!));
                    }
                    if (ConfigHandler.url?.isNotEmpty() == true) {
                        val urlString = TextComponentString(ConfigHandler.url!!)
                        urlString.style.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, urlString.text)
                        urlString.style.color = TextFormatting.AQUA
                        urlString.style.underlined = true
                        e.player.sendMessage(urlString)
                    }
                    called = true
                    return
                }
                called = false
            }
        }
    }
}