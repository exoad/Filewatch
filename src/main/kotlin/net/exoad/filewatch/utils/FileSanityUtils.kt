package net.exoad.filewatch.utils

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object FileSanityUtils
{
    fun ensureIsSaneDirectory(directoryPathString: String): Path
    {
        val targetDirectoryPath = Path.of(directoryPathString)

        if(!Files.exists(targetDirectoryPath))
        {
            try
            {
                Files.createDirectories(targetDirectoryPath)
                Logger.I.info("FileSanityUtils: Created target directory: $targetDirectoryPath")
            }
            catch(e: IOException)
            {
                Logger.I.severe("FileSanityUtils: Failed to create target directory $targetDirectoryPath: ${e.message}")
                throw IOException("Failed to create target directory: $targetDirectoryPath", e)
            }
        }
        else if(!Files.isDirectory(targetDirectoryPath))
        {
            Logger.I.severe("FileSanityUtils: Target path $targetDirectoryPath exists but is not a directory.")
            throw IOException("Target path $targetDirectoryPath is not a directory.")
        }
        return targetDirectoryPath
    }
}