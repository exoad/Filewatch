package net.exoad.filewatch.engine.data

import kotlinx.serialization.Serializable
import java.nio.file.Path
import java.util.UUID

/**
 * Represents a folder that the application is configured to watch.
 */
@Serializable
data class WatchedFolder(
    val id: String = UUID.randomUUID().toString(),
    val path: String,
    val name: String,
    val recursive: Boolean = false,
)
{
    fun toPath(): Path
    {
        return Path.of(path)
    }
}
