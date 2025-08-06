package net.exoad.filewatch.app.ephemeral

import net.exoad.filewatch.utils.Logger

object UserPreferences
{
    val defaultProperties = mapOf(
        "jobsview.show_deletion_dialog" to "true"
    )
    private val properties = mutableMapOf<String, String>()

    fun initialize()
    {
        if(DataStore.hasFile("user_preferences"))
        {
            Logger.I.info("Reloading UserPrefs from file...")
            reloadFromFile()
            if(properties.isEmpty())
            {
                Logger.I.warning("UserPrefs loaded is invalid...")
                reset()
            }
        }
        else
        {
            Logger.I.info("Starting UserPrefs from scratch...")
            DataStore.createFile("user_preferences")
            reset()
        }
    }

    operator fun get(key: String): String
    {
        require(properties.containsKey(key)) {
            "UserPreferences key '$key' does not exist for [GET]. Fix this immediately!"
        }
        return properties[key]!!
    }

    operator fun <T : Any> set(key: String, value: T)
    {
        require(properties.containsKey(key)) {
            "UserPreferences key '$key' does not exist [SET]. Fix this immediately!"
        }
        properties[key] = value.toString()
        saveToFile()
    }

    fun reset()
    {
        properties.clear()
        properties.putAll(defaultProperties)
        saveToFile()
        Logger.I.info("UserPref.properties = $properties")
    }

    fun getBool(key: String): Boolean
    {
        return this[key].toBoolean()
    }

    fun getInt(key: String): Int
    {
        return this[key].toInt()
    }

    fun getFloat(key: String): Float
    {
        return this[key].toFloat()
    }

    fun exportProperties(): Map<String, Any>
    {
        return properties.toMap()
    }

    fun reloadFromFile()
    {
        DataStore.readFile("user_preferences") { line ->
            if(line.isNotBlank())
            {
                line.split("\n").forEach {
                    val parts = it.trim().split("=", limit = 2)
                    if(parts.isNotEmpty())
                    {
                        properties[parts.first().trim()] = parts[1].trim()
                    }
                }
            }
        }
    }

    fun saveToFile()
    {
        DataStore.writeToFile("user_preferences") {
            properties.map { "${it.key}=${it.value}" }.joinToString("\n")
        }
    }
}