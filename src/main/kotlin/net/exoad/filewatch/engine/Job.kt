package net.exoad.filewatch.engine

import net.exoad.filewatch.engine.rule.Rule
import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualPath
import net.exoad.filewatch.ui.visualbuilder.VisualString
import net.exoad.filewatch.utils.Observable
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
    val creationTime: Long = System.currentTimeMillis()
    private val rules: MutableList<Rule> = mutableListOf()

    companion object : Observable<Job>()
    {
        private var nextIndex = 0
        val indexedInstances = mutableMapOf<Int, Job>()
        val hashInstances = mutableMapOf<Int, Job>()

        fun findByIndex(index: Int): Job? = indexedInstances[index]
        fun findByHash(hash: Int): Job? = hashInstances[hash]
        fun hasIndex(index: Int): Boolean = indexedInstances.containsKey(index)
        fun hasHash(hash: Int): Boolean = hashInstances.containsKey(hash)
        fun deleteAll()
        {
            indexedInstances.clear()
            hashInstances.clear()
            nextIndex = 0
        }
    }

    var hash by Delegates.notNull<Int>()
    var index by Delegates.notNull<Int>()

    init
    {
        hash = Objects.hash(folder)
        index = nextIndex++
        indexedInstances[index] = this
        hashInstances[hash] = this
        notifyObservers(this)
    }

    fun attachMonitor(newFileWatcher: (Path) -> Unit)
    {
        FileSystemMonitor.subscribe(Path(folder), newFileWatcher)
    }
}