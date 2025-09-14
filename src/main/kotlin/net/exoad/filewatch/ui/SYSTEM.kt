package net.exoad.filewatch.ui

import java.awt.Toolkit
import java.awt.datatransfer.Clipboard

fun clipboard(): Clipboard {
    return Toolkit.getDefaultToolkit().systemClipboard
}