package net.exoad.filewatch.engine

interface FileFormat
{
    val name: String
    val extensions: List<String>
    val mimeTypes: List<String>
    val category: FormatCategory
    val supportedOperations: Set<FormatOperation>
}

enum class FormatCategory
{
    IMAGE, VIDEO, AUDIO, DOCUMENT, ARCHIVE, TEXT
}

enum class FormatOperation
{
    READ, WRITE, CONVERT, COMPRESS, METADATA_EXTRACT
}

object FileFormatRegistry
{
    private val formats = mutableMapOf<String, FileFormat>()
    private val extensionMap = mutableMapOf<String, FileFormat>()

    fun register(format: FileFormat)
    {
        formats[format.name] = format
        format.extensions.forEach { extensionMap[it] = format }
    }

    fun getByExtension(extension: String): FileFormat?
    {
        return extensionMap[extension]
    }

    fun getSupportedConversions(from: FileFormat): List<FileFormat>
    {
        return formats.values.filter { target ->
            target.category == from.category && target.supportedOperations.contains(FormatOperation.WRITE) && target != from
        }
    }
}