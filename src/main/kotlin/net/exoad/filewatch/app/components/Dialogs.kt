package net.exoad.filewatch.app.components

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
import javax.swing.WindowConstants
import kotlin.system.exitProcess

fun launchErrorDialog(context: ErrorContext) {
    val copyProducer = context.copyTextProducer ?: {
        "${context.title}\n${context.description}\n${if (context.cause != null) "---\n${context.cause.stringifyTrace()}" else ""}"
    }
    JDialog().apply {
        title = "Error: ${context.title}"
        isAlwaysOnTop = true
        size = dim(550, if (context.cause != null && context.description.length > 80) 400 else 310)
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
                                                text(
                                                    context.description
                                                        .replace("\n", "<br/>")
                                                        .replace("\t", "&emsp;")
                                                )
                                            }
                                        },
                                        modifier = Modifier().apply { size = dim(450, 140) }
                                    )
                                }
                                if (context.cause != null) {
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
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                if (context.isFatal) {
                    println("Fatal Error '${context.title}'. Exiting...")
                    exitProcess(1)
                } else {
                    AppHome.frame.isEnabled = true
                }
            }
        })
    }.also {
        AppHome.frame.isEnabled = false
        it.pack()
        it.setLocationRelativeTo(AppHome.frame)
        it.isVisible = true
        it.requestFocus()
        it.toFront()
    }
}

fun launchConfirmDialog(
    title: String,
    description: String,
    showHideNextTime: Boolean = false,
    showHideNextTimeListener: (Boolean) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    JDialog().apply {
        isAlwaysOnTop = true
        this.title = title
        contentPane = scaffold(
            modifier = Modifier().apply { padding = padOnly(bottom = 8, left = 8, right = 8) },
            center = {
                +row {
                    +icon(svg("icons/help.svg", 36, 36))
                    +panel(Modifier().apply { size = dim(6, 1) })
                    +col {
                        +label(title, fontSize = 20F, bolded = true)
                        +label(
                            html {
                                span("font-size" to 14) {
                                    stripText(description)
                                }
                            },
                        )
                    }
                }
            },
            south = {
                +row(Modifier().apply { padding = padOnly(top = 20) }) {
                    if (showHideNextTime) {
                        +checkbox(false) {
                            showHideNextTimeListener(it)
                        }
                        +label("Don't Show Again", fontSize = 11F)
                    }
                    +hSpacer()
                    +button("Cancel") {
                        dispose()
                        onCancel()
                    }
                    +button("Confirm") {
                        dispose()
                        onConfirm()
                    }
                }
            }
        )
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                AppHome.frame.isEnabled = true
            }
        })
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }.also {
        AppHome.frame.isEnabled = false
        it.pack()
        it.size = dim(340, it.height + 20)
        it.preferredSize = it.size
        it.setLocationRelativeTo(AppHome.frame)
        it.isVisible = true
        it.requestFocus()
        it.toFront()
    }
}

fun launchInfoDialog(title: String, description: String) {
    JDialog().apply {
        isAlwaysOnTop = true
        this.title = title
        contentPane = scaffold(
            modifier = Modifier().apply { padding = padOnly(bottom = 8, left = 8, right = 8) },
            center = {
                +row {
                    +icon(svg("icons/info.svg", 36, 36))
                    +panel(Modifier().apply { size = dim(6, 1) })
                    +col {
                        +label(title, fontSize = 20F)
                        +label(
                            html {
                                span("font-size" to 14) {
                                    stripText(description)
                                }
                            },
                        )
                    }
                }
            },
            south = {
                +row(Modifier().apply { padding = padOnly(top = 20, left = 40) }) {
                    +hSpacer()
                    +button("Ok") {
                        dispose()
                    }
                }
            }
        )
        addWindowListener(object : WindowAdapter() {
            override fun windowClosed(e: WindowEvent?) {
                AppHome.frame.isEnabled = true
            }
        })
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }.also {
        AppHome.frame.isEnabled = false
        it.pack()
        it.size = dim(340, it.height + 20)
        it.preferredSize = it.size
        it.setLocationRelativeTo(AppHome.frame)
        it.isVisible = true
        it.requestFocus()
        it.toFront()
    }
}