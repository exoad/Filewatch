package net.exoad.filewatch.engine

/**
 * if [isFatal] is `true`, the program will crash
 */
data class ErrorContext(
    val title: String,
    val description: String,
    val help: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val cause: Throwable? = null,
    val isFatal: Boolean = true,
    val copyTextProducer: (() -> String)? = null,
)
