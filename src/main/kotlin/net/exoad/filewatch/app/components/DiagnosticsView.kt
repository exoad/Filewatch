package net.exoad.filewatch.app.components

import net.exoad.filewatch.app.components.DiagnosticsView.pumpArray
import net.exoad.filewatch.ui.ReferenceListModel
import net.exoad.filewatch.ui.listBuilder
import net.exoad.filewatch.ui.listModel
import net.exoad.filewatch.utils.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.BorderFactory

const val DIAGNOSTICS_PUMP_LOG_CAP = 16384 // make sure we dont take up too much memory automatically
val DEFAULT_TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm:ss")

fun pump(message: Any?) {
    if (pumpArray.size >= DIAGNOSTICS_PUMP_LOG_CAP) {
        pumpArray.clear()
    }
    if (message != null) {
        pumpArray.addLast(
            "[${
                DEFAULT_TIMESTAMP_FORMATTER.format(LocalDateTime.now())
            }]:&nbsp;&nbsp;$message"
        )
        (DiagnosticsView.component.model as ReferenceListModel<String>).notifyAdded(
            DiagnosticsView.component,
            pumpArray.size - 1,
            pumpArray.size - 1
        )
        DiagnosticsView.component.ensureIndexIsVisible(pumpArray.size - 1)
    } else {
        Logger.I.severe("Cannot pump a 'null' diagnostics message.")
    }
}

object DiagnosticsView {
    fun clear() {
        pumpArray.clear()
        (component.model as ReferenceListModel<String>).notifyChanges(
            component,
            0,
            pumpArray.size
        )
    }

    internal val pumpArray = ArrayDeque<String>()
    val component = listBuilder(
        listModel(
            size = { pumpArray.size },
            elementAt = { "<html>${pumpArray[it]}</html>" }
        )
    ).apply {
        border = BorderFactory.createTitledBorder("Events")
        ensureIndexIsVisible(model.size - 1)
    }
}