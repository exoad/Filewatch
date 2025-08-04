package net.exoad.filewatch.engine

import java.nio.file.Path

interface FileAction
{
    /**
     * Executes the defined action on the specified [filePath]
     * @param filePath The [Path] to the file on which the action should be performed.
     * @return `true` if the action was successful, `false` otherwise.
     */
    fun execute(filePath: Path): Boolean
}