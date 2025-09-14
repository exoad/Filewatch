package net.exoad.filewatch.app.ephemeral

import net.exoad.filewatch.utils.Logger
import kotlin.io.path.*

object DataStore {
    val rootFolder = Path("./user/")

    fun initialize() {
        Logger.I.info("Initializing the data store...")
        if (!rootFolder.exists()) {
            rootFolder.createDirectory()
        }
    }

    fun hasFile(path: String): Boolean {
        return Path("${rootFolder.absolutePathString()}/$path").exists()
    }

    fun createFile(path: String) {
        if (!hasFile(path)) {
            Path("${rootFolder.absolutePathString()}/$path").createFile()
        }
    }

    fun writeToFile(path: String, content: () -> String) {
        if (!hasFile(path)) {
            createFile(path)
        }
        val file = Path("${rootFolder.absolutePathString()}/$path")
        file.writeText(content())
    }

    fun appendToFile(path: String, content: () -> String) {
        val file = Path("${rootFolder.absolutePathString()}/$path")
        if (!hasFile(path)) {
            file.createFile()
            file.writeText(content())
        } else {
            file.appendText(content())
        }
    }

    fun readFile(path: String, consumer: (String) -> Unit) {
        if (hasFile(path)) {
            consumer(Path("${rootFolder.absolutePathString()}/$path").readText())
        }
    }
}