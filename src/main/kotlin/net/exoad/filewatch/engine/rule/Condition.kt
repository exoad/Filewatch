package net.exoad.filewatch.engine.rule

import kotlinx.serialization.Serializable
import net.exoad.filewatch.ui.visualbuilder.VisualBool
import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualLong
import net.exoad.filewatch.ui.visualbuilder.VisualString
import java.nio.file.Path

@Serializable
sealed class Condition
{
    @Serializable
    @VisualClass("File Greater Than Size", "If the file is larger than the given size, trigger the action.")
    data class FileSizeGreaterThan(
        @param:VisualLong("Size In Bytes", 0, false, "Quick Help: 1 Megabyte = 1e6 Bytes")
        val sizeBytes: Long,
    ) : Condition()

    @Serializable
    @VisualClass("File Less Than Size", "If the file is smaller than the given size, trigger the action.")
    data class FileSizeLessThan(
        @param:VisualLong("Size In Bytes", 0, false, "Quick Help: 1 Megabyte = 1e6 Bytes")
        val sizeBytes: Long,
    ) : Condition()

    @Serializable
    @VisualClass("File Name Ends With", "Checks if a file's name ends in the specified suffix.")
    data class FileNameEndsWith(
        @param:VisualString("Suffix", "", "Add a dot to check for file extension.")
        val suffix: String = "",
        @param:VisualBool("Ignore Case", true, "Whether 'a' should equal 'A' (case-sensitivity)")
        val ignoreCase: Boolean = true,
    ) : Condition()

    @Serializable
    @VisualClass("File Name Matches", "If the file name matches the regex, trigger the action.")
    data class FileNameMatches(
        @param:VisualString("Regex", "", "Please prefer to use simple regex.")
        val regex: String = "",
    ) : Condition()

    @Serializable
    @VisualClass("File Name Contains", "If the file name contains the given text, trigger the action.")
    data class FileNameContains(
        @param:VisualString("Target Content", "", "Note: This is not a regex value.")
        val substring: String = "",
        @param:VisualBool("Ignore Case", true, "Whether 'a' should equal 'A' (case-sensitivity)")
        val ignoreCase: Boolean = true,
    ) : Condition()

    @Serializable
    @VisualClass("Ignore", "Do nothing.")
    class Ignore() : Condition()

    fun isMet(filePath: Path): Boolean
    {
        return when(this)
        {
            is FileSizeGreaterThan -> filePath.toFile().length() > sizeBytes
            is FileSizeLessThan    -> filePath.toFile().length() < sizeBytes
            is FileNameMatches     -> filePath.fileName.toString().matches(Regex(regex))
            is FileNameContains    -> filePath.fileName.toString().contains(substring, ignoreCase)
            is FileNameEndsWith    -> filePath.fileName.toString().endsWith(suffix, ignoreCase)
            else                   -> false
        }
    }
}