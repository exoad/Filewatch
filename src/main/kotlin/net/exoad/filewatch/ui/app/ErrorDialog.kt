package net.exoad.filewatch.ui.app

import net.exoad.filewatch.engine.ErrorContext
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.stringifyTrace
import java.awt.datatransfer.StringSelection
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.swing.BorderFactory
import javax.swing.JDialog
import kotlin.system.exitProcess

fun launchErrorDialog(context: ErrorContext)
{
    val copyProducer = context.copyTextProducer ?: {
        "${context.title}\n${context.description}\n${if(context.cause != null) "---\n${context.cause.stringifyTrace()}" else ""}"
    }
    JDialog().apply {
        title = "Error: ${context.title}"
        isAlwaysOnTop = true
        size = dim(550, 310)
        preferredSize = size
        contentPane = scaffold(
            center = {
                +scrollPane(Modifier().apply { padding = padSym(h = 16, v = 4) }) {
                    +scaffold(
                        north = {
                            +row {
                                +icon(svg("icons/mood_bad.svg", 46, 46))
                                +panel(Modifier().apply { size = dim(6, 1) })
                                +label(
                                    html {
                                        b("font-size" to 26) {
                                            text(context.title)
                                        }
                                        br()
                                        span("font-size" to 12) {
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
                        },
                        center = {
                            +col(Modifier().apply { padding = padSym(v = 8) }) {
                                +row {
                                    +label(
                                        html {
                                            span("font-size" to 14) {
                                                text(context.description)
                                            }
                                        },
                                        modifier = Modifier().apply { size = dim(450, 140) }
                                    )
                                }
                                if(context.cause != null)
                                {
                                    +row(Modifier().apply { border = BorderFactory.createTitledBorder("Details") }) {
                                        +label(
                                            html {
                                                text(
                                                    context.cause.stringifyTrace()
                                                        .replace("\n", "<br/>")
                                                        .replace("\t", "&emsp;")
                                                )
                                            },
                                            fontSize = 12F,
                                            modifier = Modifier().apply {
                                                padding = padSym(h = 4, v = 2)
                                                alignmentX = Alignment.LEFT
                                            }
                                        )
                                    }
                                }
                                +vSpacer()
                            }
                        }
                    )
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
