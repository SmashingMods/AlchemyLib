package al132.alib.utils

import net.minecraft.util.text.translation.I18n
import java.util.*


//From JEI, MIT license https://github.com/mezz/JustEnoughItems/blob/47afa1a9f57e85060c363db447eca023ed378717/src/main/java/mezz/jei/util/Translator.java
object Translator {
    fun translateToLocal(key: String): String {
        if (I18n.canTranslate(key)) return I18n.translateToLocal(key)
        else return I18n.translateToFallback(key)
    }

    fun translateToLocalFormatted(key: String, vararg format: Any): String {
        val s = translateToLocal(key)
        return try {
            String.format(s, *format)
        } catch (e: IllegalFormatException) {
            val errorMessage = "Format error: $s"
            errorMessage
        }

    }
}

