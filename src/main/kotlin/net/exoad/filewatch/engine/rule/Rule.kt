package net.exoad.filewatch.engine.rule

import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualMultiObject

@VisualClass("Rule", "Define a condition and the action to take when it is met.")
class Rule(
    @VisualMultiObject(
        "Condition",
        hint = "Specifies how the action is triggered.",
        iconPath = "icons/checklist.svg",
        discreteValues = [
            Condition.Ignore::class,
            Condition.FileNameMatches::class,
            Condition.FileNameContains::class,
            Condition.FileSizeLessThan::class,
            Condition.FileSizeGreaterThan::class
        ]
    )
    val condition: Condition,
    @VisualMultiObject(
        "Action",
        hint = "Specifies what to do when triggered.",
        iconPath = "icons/auto_awesome.svg",
        discreteValues = [
            Action.Ignore::class,
            Action.Move::class,
            Action.Delete::class,
            Action.Convert::class,
            Action.Rename::class,
            Action.CompressImage::class,
            Action.ResizeImage::class,
        ]
    )
    val action: Action,
)
