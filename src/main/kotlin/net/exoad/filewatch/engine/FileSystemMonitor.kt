package net.exoad.filewatch.engine

import net.exoad.filewatch.engine.FileSystemMonitor.register
import net.exoad.filewatch.engine.FileSystemMonitor.start
import net.exoad.filewatch.engine.FileSystemMonitor.stop
import net.exoad.filewatch.engine.FileSystemMonitor.unregister
import net.exoad.filewatch.engine.FileSystemMonitor.watch
import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualPath
import net.exoad.filewatch.utils.Logger
import java.io.IOException
import java.nio.file.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolutePathString
import kotlin.io.path.isDirectory

@VisualClass("Watch Folder", "Supplies a path of a folder to watch for changes.")
class WatchFolderRequest(
    @VisualPath("Path", ".", "The path to the folder to watch for events.")
    val path: String = ".",
)

object FileSystemMonitor {
    private lateinit var service: WatchService
    private val keys: MutableMap<WatchKey, Path> = mutableMapOf()
    private val createListeners: MutableMap<Path, (newFile: Path) -> Unit> = mutableMapOf()
    private val deleteListeners: MutableMap<Path, (newFile: Path) -> Unit> = mutableMapOf() // TODO: map to delete event

    /**
     * Global listener used for watching when new folders are scheduled to be watched. Use normal listeners to listen
     * for individual folders.
     */
    private var globalListener = mutableListOf<((path: Path) -> Unit)>()

    fun attachGlobalListener(listener: (path: Path) -> Unit) {
        globalListener.add(listener)
        Logger.I.info("FolderWatchdog attached a global listener: ${listener.hashCode()}")
    }

    fun detachGlobalListeners() {
        globalListener.clear()
        Logger.I.info("FolderWatchdog has detached all global listeners")
    }

    private var executor = Executors.newSingleThreadExecutor()

    @Volatile
    private var running: Boolean = false

    fun isRunning(): Boolean {
        return running
    }

    val foldersWatchingCount: Int get() = keys.size
    val foldersWatching: List<Path> get() = keys.values.toList()

    fun isWatching(path: Path): Boolean {
        return keys.values.contains(path)
    }

    fun subscribe(folder: Path, listener: (newFile: Path) -> Unit) {
        createListeners[folder] = listener
    }

    fun subscribeIfNot(folder: Path, listener: (newFile: Path) -> Unit) {
        createListeners.putIfAbsent(folder) { listener }
    }

    fun isSubscribed(folder: Path): Boolean {
        return createListeners[folder] != null
    }

    /**
     * Removes the listener for [folder] if applicable.
     */
    fun unsubscribe(folder: Path) {
        createListeners.remove(folder)
    }

    /**
     * The watch key and also any potential listeners are removed
     */
    fun stopWatching(folder: Path) {
        createListeners.remove(folder)
        val watchKey = keys.filter { it.value == folder }.keys.firstOrNull()
        watchKey?.cancel()
        keys.remove(watchKey)
        Logger.I.info("FolderWatchdog Stopped watching folder: $folder")
        if (keys.isEmpty()) stopInternal()
    }

    /**
     * Initializes the service to start and prepares it for [start] to be called.
     */
    fun register() {
        service = FileSystems.getDefault().newWatchService()
        Logger.I.info("Registered Watchdog service object")
    }

    /**
     * Schedules a new folder provided by [path] to be watched by this service.
     *
     * It is required for [path] to be a valid directory
     */
    @Throws(IOException::class)
    fun watch(path: Path) {
        require(path.isDirectory())
        keys[path.register(service, StandardWatchEventKinds.ENTRY_CREATE)] = path
        globalListener.forEach { it(path) }
        Logger.I.info("FolderWatchdog WATCHING $path")
    }

    /**
     * Calls [watch] on [path] if the watchdog isn't already watching [path]
     */
    @Throws(IOException::class)
    fun watchIfNot(path: Path) {
        if (!isWatching(path)) {
            watch(path)
        }
    }

    /**
     * Actually start polling for updates from the list of registered
     * paths to watch in a separate thread.
     *
     * It is necessary to call [register] first, in order to initialize the internal
     * service object (see: [WatchService]).
     */
    @Suppress("UNCHECKED_CAST")
    fun start() {
        if (!running) {
            running = true
            if (executor.isTerminated || executor.isShutdown)
                executor = Executors.newSingleThreadExecutor()
            executor.submit {
                while (running) {
                    var key: WatchKey?
                    try {
                        key = service.take()
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        return@submit
                    }
                    key?.let { k ->
                        val dir = keys[k]
                        for (event in k.pollEvents()) {
                            val kind: WatchEvent.Kind<*>? = event.kind()
                            val ev = event as WatchEvent<Path>
                            val name = ev.context()
                            val child = dir?.resolve(name)
                            if(child != null) {
                                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                                    createListeners[dir]?.invoke(child)
                                    Logger.I.info("[ENTRY_CREATE] event ${if (createListeners[dir] == null) "ignored" else "consumed"}! (${child.absolutePathString()})")
                                }
                                if(kind == StandardWatchEventKinds.ENTRY_DELETE) {
                                    // TODO: Map to delete event
                                }
                            }
                        }
                        val valid = k.reset()
                        if (!valid) {
                            keys.remove(k)
                            if (keys.isEmpty()) {
                                stopInternal()
                            }
                        }
                    }
                }
                Logger.I.info("FolderWatchdog thread stopped.")
            }
            Logger.I.info("FolderWatchdog thread started.")
        }
    }

    /**
     * Stops the service and the associated thread.
     *
     * To dispose everything call [unregister]
     */
    fun stop() {
        running = false
        executor.shutdownNow()
        try {
            if (!executor.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                Logger.I.warning("FolderWatchdog thread did not terminate gracefully.")
            }
        } catch (_: InterruptedException) {
            Thread.currentThread().interrupt()
            Logger.I.warning("Interrupted while waiting for FolderWatchdog thread to terminate.")
        }
        Logger.I.info("FolderWatchdog thread stopped.")
    }

    private fun stopInternal() {
        running = false
    }

    /**
     * Removes everything and should be called after [stop]
     */
    fun unregister() {
        stop()
        keys.forEach { (key, _) -> key.cancel() }
        keys.clear()
        try {
            service.close()
            Logger.I.info("Watchdog service closed.")
        } catch (ex: IOException) {
            Logger.I.severe("Error closing Watchdog service: ${ex.message}")
        }
    }
}