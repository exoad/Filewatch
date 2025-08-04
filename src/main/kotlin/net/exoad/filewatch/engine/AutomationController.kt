package net.exoad.filewatch.engine

import java.nio.file.Path

object AutomationController
{
    private val jobs = mutableMapOf<Path /*folderPath*/, MutableList<Job> /*jobInstances*/>()

    fun registerJob(job: Job)
    {
        val folder = Path.of(job.folder)
        if(jobs.containsKey(folder))
        {
            jobs[folder]!!.add(job)
        }
        else
        {
            jobs[folder] = mutableListOf(job)
        }
        FileSystemMonitor.watchIfNot(folder)
    }
}