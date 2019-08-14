package al132.alib

import net.minecraftforge.common.config.Configuration
import java.io.File

object ConfigHandler {
    var config: Configuration? = null

    var announcement: String? = null
    var url: String? = null
    var tickInterval: Long? = null

    fun init(configFile: File) {
        if (config == null) {
            config = Configuration(configFile)
            load()
        }
    }

    fun load() {
        announcement = config?.getString("announcement", "Announcement", "",
                "An announcement to be periodically displayed to every player, disabled with an empty string")
        url = config?.getString("url", "Announcement", "",
                "URL to be displayed after the announcement, disabled with an empty string")
        tickInterval = config?.getInt("announcementInterval", "Announcement", 288000, 20, Integer.MAX_VALUE,
                "How frequently the announcement will be shown to all players, 4 hours by default (at 20 tps)")!!.toLong()
        if (config?.hasChanged() == true) config!!.save()
    }
}