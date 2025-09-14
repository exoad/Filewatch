package net.exoad.filewatch.utils

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.stringifyTrace(): String {
    val sw = StringWriter()
    printStackTrace(PrintWriter(sw))
    return sw.buffer.toString()
}