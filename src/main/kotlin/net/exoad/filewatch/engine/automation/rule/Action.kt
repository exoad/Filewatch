package net.exoad.filewatch.engine.automation.rule

import net.exoad.filewatch.engine.automation.FileAction
import kotlinx.serialization.Serializable
import net.exoad.filewatch.engine.automation.actions.DeleteAction
import net.exoad.filewatch.engine.automation.actions.IgnoreAction
import net.exoad.filewatch.engine.automation.actions.MoveAction
import net.exoad.filewatch.ui.visualbuilder.VisualBool
import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualDiscreteString
import net.exoad.filewatch.ui.visualbuilder.VisualDouble
import net.exoad.filewatch.ui.visualbuilder.VisualLong
import net.exoad.filewatch.ui.visualbuilder.VisualPath
import net.exoad.filewatch.ui.visualbuilder.VisualString

@Serializable
sealed class Action
{
    @Serializable
    @VisualClass("Convert File", "Convert this file into another format.")
    data class Convert(
        @param:VisualDiscreteString("To", "", [])
        val targetFormat: String,
        @param:VisualBool("Delete Original", false)
        val deleteOriginal: Boolean = false,
        @param:VisualPath("Output Folder", ".")
        val outputDirectory: String = ".",
    ) : Action()

    @Serializable
    @VisualClass("Resize Image", "Resize the image to new dimensions.")
    data class ResizeImage(
        @param:VisualLong("New Width", 0, false)
        val width: Long,
        @param:VisualLong("New Height", 0, false)
        val height: Long,
        @param:VisualBool("Keep Aspect Ratio", true)
        val keepAspectRatio: Boolean = true,
        @param:VisualBool("Delete Original", false)
        val deleteOriginal: Boolean = false,
        @param:VisualPath("Output Folder", ".")
        val outputDirectory: String = ".",
    ) : Action()

    @Serializable
    @VisualClass("Compress Image", "Compress the image to save disk space.")
    data class CompressImage(
        @param:VisualDouble(
            "Quality %",
            85.0,
            allowNegative = false,
            hint = "The sharpness of the output. A higher quality will mean more disk usage."
        )
        val quality: Double = 85.0,
        @param:VisualBool("Delete Original", false, "Remove the original file.")
        val deleteOriginal: Boolean = false,
        @param:VisualPath("Output Folder", ".", "Where to place the outputted file.")
        val outputDirectory: String = ".",
    ) : Action()

    @Serializable
    @VisualClass("Rename File", "Rename the file using a custom name pattern.")
    data class Rename(
        // TODO: use messageformat for this
        @param:VisualString(
            "New Name", "", "<html>Formatting Tags:<br/>{filename} - Original File Name<br/>{date} - Time this event " +
                    "in " +
                    "MM_DD_YYYY_HH_MM_SS<br/></html>"
        )
        val newNamePattern: String,
        @param:VisualBool("Delete Original", false, "Remove the original file.")
        val deleteOriginal: Boolean = false,
        @param:VisualPath("Output Folder", ".", "Where to place the outputted file.")
        val outputDirectory: String = ".",
    ) : Action()

    @Serializable
    @VisualClass("Move File", "Move a file to a selected folder.")
    data class Move(
        @param:VisualPath("Output Folder", ".", hint = "A folder to move the file to.")
        val targetDirectory: String,
        @param:VisualBool("Overwrite?", false, hint = "If a file with the same name exists. It will be overwritten.")
        val overwrite: Boolean = false,
    ) : Action()

    @Serializable
    @VisualClass("Delete File", "Permanently delete this file.")
    class Delete() : Action()

    @Serializable
    @VisualClass("Ignore", "Do nothing.")
    class Ignore() : Action()

    fun toExecutableAction(): FileAction
    {
        return when(this)
        {
            is Move   -> MoveAction(this)
            is Delete -> DeleteAction
            else      -> IgnoreAction
        }
    }
}