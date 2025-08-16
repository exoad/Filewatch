package net.exoad.filewatch

import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme
import net.exoad.filewatch.engine.ErrorContext
import net.exoad.filewatch.engine.FileSystemMonitor
import net.exoad.filewatch.app.components.AppHome
import net.exoad.filewatch.app.components.pump
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.app.components.launchErrorDialog
import net.exoad.filewatch.app.ephemeral.DataStore
import net.exoad.filewatch.app.ephemeral.UserPreferences
import net.exoad.filewatch.utils.Chronos
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import java.net.URL
import java.text.SimpleDateFormat
import javax.swing.UIManager
import kotlin.io.path.absolutePathString
import kotlin.jvm.java

object FileWatch
{
    val isDevBuild = System.getenv("isDevBuild").equals("true", ignoreCase = true)

    fun javaClass(): Class<FileWatch>
    {
        return FileWatch::class.java
    }

    fun getResource(location: String): URL?
    {
        return javaClass().getResource(location)
    }
}

val start = Chronos.currentMillis()

fun main(args: Array<String>)
{
    System.setProperty("sun.java2d.opengl", "True")
    Logger.I.info("Starting AutoFile")
    DataStore.initialize()
    UserPreferences.initialize()
    UIManager.setLookAndFeel(FlatSpacegrayIJTheme())
    Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        launchErrorDialog(
            ErrorContext(
                "Uncaught Exception",
                "${throwable.message ?: ""}\nThread:\n ${thread.name}#${thread.id}\n- State: ${
                    thread.state
                }\n- Priority: ${thread.priority}\n- Group: ${thread.threadGroup}",
                cause = throwable,
                isFatal = true,
            )
        )
    }
    FileSystemMonitor.register()
    FileSystemMonitor.start()
    AppHome.show()
    FileSystemMonitor.attachGlobalListener {
        pump(
            html {
                span("color" to Theme.HTML_BLUE) {
                    text("Watch Folder: ${it.absolutePathString()}")
                }
            }
        )
    }
    pump(
        html {
            b {
                text(
                    "${if(FileWatch.isDevBuild) "(DevBuild) " else ""}Service woke up at ${
                        SimpleDateFormat().format(start)
                    }"
                )
            }
        }
    )
    Runtime.getRuntime().addShutdownHook(Thread {
        FileSystemMonitor.unregister()
    })
    if(FileWatch.isDevBuild)
    {
        delegatedMain(args)
    }
}