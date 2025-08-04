package net.exoad.filewatch.engine

data class Task(val job: Job, val filePath: String, val timestamp: Long)