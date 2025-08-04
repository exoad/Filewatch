package net.exoad.filewatch.utils

val EMPTY = Any()

fun String.truncate(maxLength: Int, ellipsis: String = "..."): String
{
    return when
    {
        this.length <= maxLength -> this
        else                     -> this.take(maxLength - ellipsis.length) + ellipsis
    }
}

object Theme
{
    const val SUCCESS_COLOR = 0x3c4c2a
    const val ERROR_COLOR = 0x993737
    const val NULL_COLOR = 0xff00ff
    const val WARN_COLOR = 0x6e5512

    // html color codes for use within swing html components
    const val HTML_RED = "#E25D5D"
    const val HTML_BLUE = "#618AD6"
    const val HTML_YELLOW = "#D6A561"
}