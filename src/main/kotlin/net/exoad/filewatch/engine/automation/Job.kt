package net.exoad.filewatch.engine.automation

import net.exoad.filewatch.engine.automation.rule.Rule
import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualPath
import net.exoad.filewatch.ui.visualbuilder.VisualString
import java.nio.file.Path
import java.util.Objects
import kotlin.io.path.Path
import kotlin.properties.Delegates

@VisualClass("Job", "Constructs a job that watches for events within a folder.")
class Job(
    @VisualString("Name", "", "Something for you to remember this job as.")
    val canonicalName: String = "",
    @VisualPath("Folder", "", "The folder to watch for events.")
    val folder: String,
)
{
    val creationTime: Long
    val rules: List<Rule>

    companion object
    {
        private val instances = mutableMapOf<Int, Job>()

        fun find(hash: Int): Job?
        {
            return instances[hash]
        }

        fun has(hash: Int): Boolean
        {
            return instances.contains(hash)
        }

        fun deleteAll()
        {
            instances.clear()
        }
    }

    var hash by Delegates.notNull<Int>()

    init
    {
        rules = emptyList()
        creationTime = System.currentTimeMillis()
        hash = Objects.hash(folder, rules, creationTime)
        instances[hash] = this
    }

    fun attachMonitor(newFileWatcher: (Path) -> Unit)
    {
        FileSystemMonitor.subscribe(Path(folder), newFileWatcher)
    }
}