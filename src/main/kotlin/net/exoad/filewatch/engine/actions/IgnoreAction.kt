package net.exoad.filewatch.engine.actions

import net.exoad.filewatch.engine.FileAction
import java.nio.file.Path

object IgnoreAction : FileAction {
    override fun execute(filePath: Path): Boolean {
        return true
    }
}