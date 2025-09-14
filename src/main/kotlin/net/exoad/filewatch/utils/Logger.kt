package net.exoad.filewatch.utils

import net.exoad.filewatch.FileWatch
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

object Logger {
    @JvmStatic
    val I: Logger = Logger.getLogger("net.exoad:AutoFile")
    var loggerListener: ((LogRecord?) -> Unit)? = null

    init {
        System.setProperty(
            "java.util.logging.SimpleFormatter.format",
            $$"%1$tH:%1$tM:%1$tS [%4$-7s]: %5$s%6$s%n"
        )
        if (FileWatch.isDevBuild) {
            I.level = Level.INFO
            I.addHandler(object : ConsoleHandler() {
                override fun publish(record: LogRecord?) {
                    loggerListener?.invoke(record)
                }
            })
        } else {
            I.level = Level.OFF
        }
    }
}