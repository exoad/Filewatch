package net.exoad.filewatch

import com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme
import net.exoad.filewatch.engine.automation.FileSystemMonitor
import net.exoad.filewatch.ui.app.AppHome
import net.exoad.filewatch.ui.app.pump
import net.exoad.filewatch.utils.Logger
import java.net.URL
import java.text.SimpleDateFormat
import javax.swing.UIManager
import kotlin.io.path.absolutePathString
import kotlin.jvm.java

object Filewatch
{
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
            "<span style='color: #4a8fcf'>Watch Folder: ${it.absolutePathString()}</span>"
        )
    }
    pump("<b>Service woke up at ${SimpleDateFormat().format(start)}</b>")
    Runtime.getRuntime().addShutdownHook(Thread {
        FileSystemMonitor.unregister()
    })
}