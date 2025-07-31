package net.exoad.filewatch.engine.automation

import java.nio.file.Path

data class FileEvent(val path: Path, val watchedDirectory: Path)