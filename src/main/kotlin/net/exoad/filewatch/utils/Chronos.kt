package net.exoad.filewatch.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Chronos
{
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss")

    fun formatTime(format: String? = null): String
    {
        return (if(format != null) DateTimeFormatter.ofPattern(format) else formatter).format(LocalDateTime.now())
    }

    fun formatTime(timestamp: Long, format: String? = null, zoneId: ZoneId = ZoneId.systemDefault()): String
    {
        return (if(format != null) DateTimeFormatter.ofPattern(format) else formatter).format(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zoneId)
        )
    }

    fun currentMillis(): Long
    {
        return System.currentTimeMillis()
    }
}