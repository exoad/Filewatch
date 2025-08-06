package net.exoad.filewatch.engine.actions

import net.exoad.filewatch.engine.FileAction
import net.exoad.filewatch.app.components.pump
import net.exoad.filewatch.ui.html
import net.exoad.filewatch.ui.span
import net.exoad.filewatch.ui.text
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
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
            pump(
                html {
                    span("color" to Theme.HTML_YELLOW) {
                        text("Could not [DELETE] non-existent $filePath.")
                    }
                }
            )
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
            Logger.I.severe("DeleteAction: Failed to [DELETE] file $filePath: ${e.message}")
            pump(
                html {
                    span("color" to Theme.HTML_RED) {
                        text("Failed to [DELETE] file $filePath because of ${e.message}")
                    }
                }
            )
            return false
        }
        catch(e: SecurityException)
        {
            Logger.I.severe("DeleteAction: Permission denied when deleting file $filePath: ${e.message}")
            pump(
                html {
                    span("color" to Theme.HTML_RED) {
                        text("OS denied permission to delete $filePath")
                    }
                }
            )
            return false
        }
    }
}