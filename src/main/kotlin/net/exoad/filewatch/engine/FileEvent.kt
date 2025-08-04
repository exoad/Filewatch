package net.exoad.filewatch.engine

import java.nio.file.Path

data class FileEvent(val path: Path, val watchedDirectory: Path)