package net.exoad.filewatch.engine.automation.actions

import net.exoad.filewatch.engine.automation.FileAction
import java.nio.file.Path

object IgnoreAction : FileAction
{
    override fun execute(filePath: Path): Boolean
    {
        return true
    }
}