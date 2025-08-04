package net.exoad.filewatch

import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme
import net.exoad.filewatch.engine.AutomationController
import net.exoad.filewatch.engine.ErrorContext
import net.exoad.filewatch.engine.FileSystemMonitor
import net.exoad.filewatch.engine.Job
import net.exoad.filewatch.ui.app.AppHome
import net.exoad.filewatch.ui.app.pump
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.ui.app.launchErrorDialog
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import java.net.URL
import java.text.SimpleDateFormat
import javax.swing.UIManager
import kotlin.io.path.absolutePathString
import kotlin.jvm.java

object Filewatch
{
    val isDevBuild = System.getenv("isDevBuild").equals("true", ignoreCase = true)

    fun javaClass(): Class<Filewatch>
    {
        return Filewatch::class.java
    }

    fun getResource(location: String): URL?
    {
        return javaClass().getResource(location)
    }
}

val start = System.currentTimeMillis()

fun main()
{
    System.setProperty("sun.java2d.opengl", "True")
    Logger.I.info("Starting AutoFile")
    UIManager.setLookAndFeel(FlatSpacegrayIJTheme())
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
                    "${if(Filewatch.isDevBuild) "(DevBuild)" else ""} Service woke up at ${
                        SimpleDateFormat().format(start)
                    }"
                )
            }
        }
    )
    Runtime.getRuntime().addShutdownHook(Thread {
        FileSystemMonitor.unregister()
    })
    AutomationController.registerJob(Job("Downloads", "C:\\Users\\error\\Downloads\\"))
    launchErrorDialog(
        ErrorContext(
            title = "Uh oh!",
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt " +
                    "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
            cause = Exception("Amogus")
        )
    )
}