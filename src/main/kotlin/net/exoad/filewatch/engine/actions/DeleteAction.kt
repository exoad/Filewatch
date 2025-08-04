package net.exoad.filewatch.engine.actions

import net.exoad.filewatch.engine.FileAction
import net.exoad.filewatch.utils.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

object DeleteAction : FileAction
{
    override fun execute(filePath: Path): Boolean
    {
        if(!Files.exists(filePath))
        {
            Logger.I.warning("DeleteAction: File does not exist at path: $filePath. Cannot delete.")
            return false
        }
        try
        {
            Files.delete(filePath)
            Logger.I.info("DeleteAction: Successfully deleted file: $filePath")
            return true
        }
        catch(e: IOException)
        {
            Logger.I.severe("DeleteAction: Failed to delete file $filePath: ${e.message}")
            return false
        }
        catch(e: SecurityException)
        {
            Logger.I.severe("DeleteAction: Permission denied when deleting file $filePath: ${e.message}")
            return false
        }
    }
}