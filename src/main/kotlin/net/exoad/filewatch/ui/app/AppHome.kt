package net.exoad.filewatch.ui.app

import net.exoad.filewatch.engine.automation.FileSystemMonitor
import net.exoad.filewatch.engine.automation.WatchFolderRequest
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.ui.visualbuilder.VisualBuilder
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import javax.swing.SwingUtilities
import javax.swing.UIManager
import javax.swing.WindowConstants
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

object AppHome
{
    val frame = frame("Filewatch").apply {
        val watchListListener = listen()
        pack()
        size = dim(850, 600)
        preferredSize = size
        contentPane = scaffold(
            Modifier().apply { padding = padAll(8) },
            north = {
                +row(spacing = 2) {
                    +button(
                        "Watch Folder",
                        modifier = Modifier().apply { },
                        icon = svg("icons/eye.svg")
                    ) {
                        VisualBuilder.build(WatchFolderRequest::class).apply {
                            onBuild = { obj ->
                                FileSystemMonitor.watch(Path(obj.path))
                                watchListListener()
                            }
                        }.showNow()
                    }
                    +toggleButton {
                        return@toggleButton if(it)
                        {
                            ToggleButtonState(
                                icon = svg("icons/pause.svg"),
                                backgroundColor = color(Theme.SUCCESS_COLOR),
                                boldedLabel = true,
                                tooltip = "Pause Service"
                            )
                        }
                        else
                        {
                            ToggleButtonState(
                                icon = svg("icons/play_arrow.svg"),
                                backgroundColor = UIManager.getColor("Button.background"),
                                boldedLabel = false,
                                tooltip = "Start Service"
                            )
                        }
                    }
                    +hSpacer()
                    +iconButton(svg("icons/settings.svg")) {}
                    +button(
                        "Stop",
                        boldedLabel = true,
                        icon = svg("icons/stop.svg"),
                        backgroundColor = color(Theme.ERROR_COLOR)
                    ) {}
                }
            },
            center = {
                +splitPane(
                    modifier = Modifier().apply { padding = padSym(v = 8) },
                    axis = Axis.VERTICAL,
                    dividerPosition = (this@apply.size.height * 0.6).toInt(),
                    first = {
                        +splitPane(
                            dividerPosition = (this@apply.size.width * 0.65).toInt(),
                            first = {
                                +scrollPane {
                                    +listBuilder(
                                        listModel<String>(
                                            size = { FileSystemMonitor.foldersWatchingCount },
                                            elementAt = { FileSystemMonitor.foldersWatching[it].absolutePathString() },
                                        )
                                    ).apply {
                                        watchListListener.observe {
                                            Logger.I.info("Refresh Folder Watch List Modal")
                                            (model as ReferenceListModel<String>).notifyChanges(
                                                model,
                                                0,
                                                model.size - 1
                                            )
                                        }
                                    }
                                }
                            },
                            second = {
                                +button("Actions")
                            }
                        )
                    },
                    second = {
                        +scaffold(
                            center = {
                                +scrollPane {
                                    +DiagnosticsView.component
                                }
                            },
                            east = {
                                +col(Modifier().apply { padding = padOnly(left = 4) }) {
                                    +iconButton(svg("icons/cleaning_services.svg")) {
                                        DiagnosticsView.clear()
                                    }
                                }
                            }
                        )
                    }
                )
                defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
            },
//            south = {
//                +row(spacing = 2) {
//                    val memoryUsagePoller = refresh(.0, period = 600) {
//                        (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime
//                            .getRuntime().totalMemory() * 100
//                    }
//                    +progressBar(stringPainted = true, string = "Memory Usage").apply {
//                        memoryUsagePoller.observe {
//                            value = it.toInt()
//                            string = "${
//                                ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1e6).toInt()
//                            } Mb / ${(Runtime.getRuntime().totalMemory() / 1e6).toInt()} Mb"
//                        }
//                    }
//                    memoryUsagePoller.start()
//                    +hSpacer()
//                }
//            }
        )
    }

    fun show()
    {
        SwingUtilities.invokeLater {
            frame.location = centerScreenRect(frame.size.toRect())
            frame.isVisible = true
        }
    }

    fun hide()
    {
        SwingUtilities.invokeLater {
            frame.isVisible = false
        }
    }

    fun dispose()
    {
        SwingUtilities.invokeLater {
            frame.dispose()
        }
    }
}