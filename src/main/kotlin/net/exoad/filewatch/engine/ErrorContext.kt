package net.exoad.filewatch.engine

import net.exoad.filewatch.utils.Chronos

/**
 * if [isFatal] is `true`, the program will crash
 */
data class ErrorContext(
    val title: String,
    val description: String,
    val help: String? = null,
    val timestamp: Long = Chronos.currentMillis(),
    val cause: Throwable? = null,
    val isFatal: Boolean = true,
    val copyTextProducer: (() -> String)? = null,
)
