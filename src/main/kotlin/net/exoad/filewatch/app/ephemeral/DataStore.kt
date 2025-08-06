package net.exoad.filewatch.app.ephemeral

import net.exoad.filewatch.utils.Logger
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

object DataStore
{
    val rootFolder = Path("./user/")

    fun initialize()
    {
        Logger.I.info("Initializing the data store...")
        if(!rootFolder.exists())
        {
            rootFolder.createDirectory()
        }
    }

    fun hasFile(path: String): Boolean
    {
        return Path("${rootFolder.absolutePathString()}/$path").exists()
    }

    fun createFile(path: String)
    {
        if(!hasFile(path))
        {
            Path("${rootFolder.absolutePathString()}/$path").createFile()
        }
    }

    fun writeToFile(path: String, content: () -> String)
    {
        if(!hasFile(path))
        {
            createFile(path)
        }
        val file = Path("${rootFolder.absolutePathString()}/$path")
        file.writeText(content())
    }

    fun readFile(path: String, consumer: (String) -> Unit)
    {
        if(hasFile(path))
        {
            consumer(Path("${rootFolder.absolutePathString()}/$path").readText())
        }
    }
}