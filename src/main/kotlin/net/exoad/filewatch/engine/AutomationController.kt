package net.exoad.filewatch.engine

import net.exoad.filewatch.app.components.pump
import net.exoad.filewatch.engine.rule.Rule
import net.exoad.filewatch.ui.html
import net.exoad.filewatch.ui.span
import net.exoad.filewatch.ui.text
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import java.nio.file.Path

object AutomationController {
    private val jobs = mutableMapOf<Path /*folderPath*/, MutableList<Job> /*jobInstances*/>()

    fun hasFolder(path: String): Boolean {
        return jobs.containsKey(Path.of(path))
    }

    fun getJob(path: String): MutableList<Job>? {
        return jobs[Path.of(path)]
    }

    fun removeJob(job: Job) {
        val folder = Path.of(job.folder)
        if (jobs.containsKey(folder)) {
            jobs.remove(folder)
            Logger.I.info("AutomationController removed $folder's job")
            pump(html {
                span("color" to Theme.HTML_GREEN) {
                    text("Removed $folder's job")
                }
            })
        } else {
            Logger.I.warning("AutomationController could not remove $folder's job because it doesn't exist.")
            pump(
                html {
                    span("color" to Theme.HTML_RED) {
                        text("Could not remove $folder's job because it does not exist.")
                    }
                }
            )
        }
    }

    fun registerJob(job: Job) {
        val folder = Path.of(job.folder)
        if (jobs.containsKey(folder)) {
            jobs[folder]!!.add(job)
        } else {
            jobs[folder] = mutableListOf(job)
        }
        FileSystemMonitor.watchIfNot(folder)
    }

    fun registerRuleToJob(job: Job, vararg rule: Rule) {
        if (!hasFolder(job.folder)) {
            registerJob(job)
        }
        val path = Path.of(job.folder)
        for (j in jobs[path]!!) {
            if (j == job) {
                j.rules.addAll(rule)
            }
        }
    }
}