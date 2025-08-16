package net.exoad.filewatch.app.components

import net.exoad.filewatch.app.ephemeral.UserPreferences
import net.exoad.filewatch.engine.AutomationController
import net.exoad.filewatch.engine.Job
import net.exoad.filewatch.engine.JobCreationEventType
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.utils.Chronos
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import java.awt.Component
import javax.swing.AbstractCellEditor
import javax.swing.BorderFactory
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.UIManager
import javax.swing.table.AbstractTableModel
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

object JobsView
{
    class JobCellRendererEditor : AbstractCellEditor(), TableCellRenderer, TableCellEditor
    {
        private var currentJob: Job? = null

        private fun buildPanel(job: Job): JPanel
        {
            return row(Modifier().apply {
                border = BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIManager.getColor("Separator.foreground"), 1, true),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)
                )
            }) {
                +col {
                    +label(
                        html {
                            b("font-size" to 16) {
                                text(job.canonicalName)
                            }
                        }
                    )
                    +label(
                        html {
                            span("font-size" to 12) {
                                text("Folder: ${job.folder}")
                                br()
                                text("Created At: ${Chronos.formatTime(job.creationTime)}")
                            }
                        }
                    )
                }
                +label(
                    html {
                        span("font-size" to 11) {
                            em {
                                text("Rules ")
                            }
                            text(job.rules.size.toString())
                        }
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
                    ) {
                        fun delete()
                        {
                            AutomationController.removeJob(job)
                            Job.notifyObservers(JobCreationEventType.DELETE)
                        }
                        if(UserPreferences.getBool("jobsview.show_deletion_dialog"))
                        {
                            launchConfirmDialog(
                                "Are you sure?",
                                "Confirm deletion of this job?",
                                showHideNextTime = true,
                                showHideNextTimeListener = {
                                    UserPreferences["jobsview.show_deletion_dialog"] = !it
                                },
                                onCancel = {},
                                onConfirm = ::delete
                            )
                        }
                        else
                        {
                            delete()
                        }
                    }
                }
            }
        }

        override fun getTableCellRendererComponent(
            table: JTable,
            value: Any?,
            isSelected: Boolean,
            hasFocus: Boolean,
            row: Int,
            column: Int,
        ): Component
        {
            return buildPanel(value as? Job ?: return JLabel("Invalid Job"))
        }

        override fun getTableCellEditorComponent(
            table: JTable,
            value: Any?,
            isSelected: Boolean,
            row: Int,
            column: Int,
        ): Component
        {
            return buildPanel((value as? Job)!!)
        }

        override fun getCellEditorValue(): Any?
        {
            return currentJob
        }
    }

    init
    {
        Job.observe { eventType ->
            pump(html { span("color" to Theme.HTML_YELLOW) { text("Refreshing Jobs Listings") } })
            val last = if(Job.hashInstances.isNotEmpty()) Job.hashInstances.size - 1 else 0
            Logger.I.info("JobsView EvenType: [$eventType] at $last")
            (table.model as AbstractTableModel).let {
                when(eventType)
                {
                    JobCreationEventType.ADD -> it::fireTableRowsInserted
                    JobCreationEventType.DELETE -> it::fireTableRowsDeleted
                    JobCreationEventType.UPDATE -> it::fireTableRowsUpdated
                }
            }(last, last)
        }
    }

    val table = JTable(object : AbstractTableModel()
    {
        override fun getRowCount(): Int
        {
            return Job.hashInstances.size
        }

        override fun getColumnCount(): Int
        {
            return 1
        }

        override fun getColumnName(col: Int): String
        {
            return "Jobs"
        }

        override fun getValueAt(row: Int, col: Int): Job
        {
            return Job.indexedInstances[row]!!
        }

        override fun isCellEditable(row: Int, col: Int): Boolean
        {
            return true
        }
    }).apply {
        rowHeight = 80
        setDefaultRenderer(Any::class.java, JobCellRendererEditor())
        setDefaultEditor(Any::class.java, JobCellRendererEditor())
        tableHeader = null
    }
    val component = scrollPane { +table }
}
