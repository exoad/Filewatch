package net.exoad.filewatch.ui.app

import net.exoad.filewatch.engine.Job
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import java.awt.Component
import javax.swing.BorderFactory
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.UIManager

object JobsView
{
    init
    {
        Job.observe {
            Logger.I.info("JobsView Refreshing...")
            pump(html { span("color" to Theme.HTML_YELLOW) { text("Refreshing Jobs Listings") } })
            val last = Job.hashInstances.size - 1
            (list.model as ReferenceListModel<Job>).notifyChanges(list.model, last, last)
        }
    }

    val list = listBuilder(
        items = listModel<Job>(
            size = { Job.hashInstances.size },
            elementAt = { Job.indexedInstances[it]!! }
        )
    ).apply {
        cellRenderer = object : ListCellRenderer<Job>
        {
            override fun getListCellRendererComponent(
                list: JList<out Job?>?,
                value: Job?,
                index: Int,
                isSelected: Boolean,
                cellHasFocus: Boolean,
            ): Component?
            {
                if(value != null && list != null)
                {
                    //we dont really care about focus
                    return row(Modifier().apply {
                        border = BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground"), 1, true),
                            padSym(v = 2, h = 8)
                        )
                    }) {
                        +label(
                            html {
                                b("font-size" to 14) {
                                    text(value.canonicalName)
                                }
                                br()
                                span("font-size" to 12) {
                                    text(value.folder)
                                }
                            },
                            modifier = Modifier().apply {
                                alignmentX = Alignment.LEFT
                                alignmentY = Alignment.CENTER
                            }
                        )
                        +hSpacer()
                        +col {
                            +button(
                                "Add Rule",
                                icon = svg("icons/add.svg"),
                                modifier = Modifier().apply {
                                    alignmentX = Alignment.RIGHT
                                }
                            )
                            +button(
                                "Delete",
                                icon = svg("icons/delete.svg"),
                                modifier = Modifier().apply {
                                    alignmentX = Alignment.RIGHT
                                }
                            )
                        }
                    }
                }
                return null
            }
        }
    }
    val component = scrollPane { +list }
}