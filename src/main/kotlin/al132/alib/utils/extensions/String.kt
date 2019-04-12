package al132.alib.utils.extensions

import al132.alib.utils.Translator
import al132.alib.utils.Utils

/**
 * Created by al132 on 4/27/2017.
 */


fun String.toDict(prefix: String) = Utils.toDictName(prefix, this)

fun String.translate(): String = Translator.translateToLocal(this)