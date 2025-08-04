package net.exoad.filewatch.engine.actions

import net.exoad.filewatch.engine.FileAction
import net.exoad.filewatch.engine.rule.Action
import net.exoad.filewatch.utils.FileSanityUtils
import net.exoad.filewatch.utils.Logger
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

class MoveAction(private val moveFileAction: Action.Move) : FileAction
{
    override fun execute(filePath: Path): Boolean
    {
        if(!Files.exists(filePath))
        {
            Logger.I.warning("MoveAction: File does not exist at path: $filePath. Cannot move.")
            return false
        }
        val targetDirectoryPath: Path
        try
        {
            targetDirectoryPath = FileSanityUtils.ensureIsSaneDirectory(moveFileAction.outputDirectory)
        }
        catch(_: IOException)
        {
            return false
        }
        val destinationPath = targetDirectoryPath.resolve(filePath.fileName)
        try
        {
            val options = mutableListOf<StandardCopyOption>()
            if(moveFileAction.overwrite)
            {
                options.add(StandardCopyOption.REPLACE_EXISTING)
            }
            options.add(StandardCopyOption.ATOMIC_MOVE)
            Files.move(filePath, destinationPath, *options.toTypedArray())
            Logger.I.info("MoveAction: Successfully moved file from $filePath to $destinationPath")
            return true
        }
        catch(e: IOException)
        {
            Logger.I.severe("MoveAction: Failed to move file from $filePath to $destinationPath: ${e.message}")
            return false
        }
        catch(e: SecurityException)
        {
            Logger.I.severe("MoveAction: Permission denied when moving file $filePath to $destinationPath: ${e.message}")
            return false
        }
    }
}
