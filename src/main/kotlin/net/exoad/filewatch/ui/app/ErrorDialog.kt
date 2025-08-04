package net.exoad.filewatch.ui.app

import net.exoad.filewatch.engine.ErrorContext
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.utils.Logger
import java.awt.datatransfer.StringSelection
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.swing.JDialog
import kotlin.system.exitProcess

fun launchErrorDialog(context: ErrorContext)
{
    val copyProducer = context.copyTextProducer ?: {
        "${context.title}\n${context.description}\n${if(context.cause != null) "---\n${context.cause}" else ""}"
    }
    JDialog().apply {
        title = "Error: ${context.title}"
        isAlwaysOnTop = true
        size = dim(400, 180)
        preferredSize = size
        contentPane = scaffold(
            center = {
                +col(Modifier().apply { padding = padSym(h = 16, v = 4) }) {
                    +row {
                        +icon(svg("icons/mood_bad.svg", 32, 32))
                        +panel(Modifier().apply { size = dim(6, 1) })
                        +label(
                            html {
                                b("font-size" to 26) {
                                    text(context.title)
                                }
                                br()
                                em("font-size" to 14) {
                                    text(
                                        DEFAULT_TIMESTAMP_FORMATTER.format(
                                            LocalDateTime.ofInstant(
                                                Instant.ofEpochMilli(context.timestamp),
                                                ZoneId.systemDefault()
                                            )
                                        )
                                    )
                                }
                            }
                        )
                    }
                    +vSpacer()
                }
            },
            south = {
                +row(Modifier().apply { padding = padSym(h = 6, v = 4) }) {
                    +hSpacer()
                    +button("Copy To Clipboard", outlined = true) {
                        clipboard().setContents(StringSelection(copyProducer()), null)
                        Logger.I.fine("Copied error context data to clipboard!")
                    }
                    +button("Ok") {
                        dispose()
                    }
                }
            }
        )
        addWindowListener(object : WindowAdapter()
        {
            override fun windowClosed(e: WindowEvent?)
            {
                if(context.isFatal)
                {
                    println("Fatal Error '${context.title}'. Exiting...")
                    exitProcess(1)
                }
                else
                {
                    AppHome.frame.isEnabled = true
                }
            }
        })
    }.also {
        AppHome.frame.isEnabled = false
        it.pack()
        it.setLocationRelativeTo(AppHome.frame)
        it.isVisible = true
    }
}
