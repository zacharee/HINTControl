@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")
package dev.zwander.common.util

import dev.zwander.common.data.HistoricalSnapshot
import dev.zwander.common.model.MainModel
import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.file.utils.FILE_SYSTEM
import io.github.xxfast.kstore.utils.StoreDispatcher
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okio.Path
import okio.Path.Companion.toPath

expect fun pathTo(subPath: String, startingTag: String): String

class CreatingCodec<T : @Serializable Any>(
    private val file: Path,
    private val json: Json,
    private val serializer: KSerializer<T>,
    private val wrappedCodec: FileCodec<T> = FileCodec(file, json, serializer),
) : Codec<T> by wrappedCodec {
    companion object {
        inline operator fun <reified T: @Serializable Any> invoke(
            file: Path,
            json: Json,
        ) = CreatingCodec<T>(
            file = file,
            json = json,
            serializer = json.serializersModule.serializer(),
        )
    }

    init {
        ensureCreated()
    }

    override suspend fun decode(): T? {
        return try {
            wrappedCodec.decode()
        } catch (e: Throwable) {
            CrossPlatformBugsnag.notify(IllegalStateException("Unable to decode JSON from file, deleting stored data.", e))
            encode(null)
            null
        }
    }

    override suspend fun encode(value: T?) {
        wrappedCodec.encode(value)
    }

    private fun ensureCreated() {
        file.parent?.let {
            if (!FILE_SYSTEM.exists(it)) {
                FILE_SYSTEM.createDirectories(it)
            }
        }
    }
}

class KStore<T : @Serializable Any>(
    private val default: T? = null,
    private val enableCache: Boolean = true,
    private val codec: Codec<T>,
) {
    private val lock: Mutex = Mutex()
    internal val cache: MutableStateFlow<T?> = MutableStateFlow(default)

    /** Observe store for updates */
    val updates: Flow<T?>
        get() = this.cache
            .onStart {
                lock.withLock {
                    read(fromCache = false)
                }
            } // updates will always start with a fresh read

    private suspend fun write(value: T?): Unit = withContext(StoreDispatcher) {
        codec.encode(value)
        cache.emit(value)
    }

    private suspend fun read(fromCache: Boolean): T? = withContext(StoreDispatcher) {
        if (fromCache && cache.value != default) return@withContext cache.value
        val decoded: T? = codec.decode()
        val emitted: T? = decoded ?: default
        cache.emit(emitted)
        return@withContext emitted
    }

    /**
     * Set a value to the store
     *
     * @param value to set
     */
    suspend fun set(value: T?): Unit = lock.withLock { write(value) }

    /**
     * Get a value from the store
     *
     * @return value stored/cached (if enabled)
     */
    suspend fun get(): T? = lock.withLock { read(enableCache) }

    /**
     * Update a value in a store.
     * Note: This maintains a single mutex lock for both get and set
     *
     * @param operation lambda to update a given value of type [T]
     */
    suspend fun update(operation: (T?) -> T?): Unit = lock.withLock {
        val previous: T? = read(enableCache)
        val updated: T? = operation(previous)
        write(updated)
    }

    /**
     * Set the value of the store to null
     */
    suspend fun delete() {
        set(null)
        cache.emit(null)
    }

    /**
     * Set the value of the store to the default
     */
    suspend fun reset() {
        set(default)
        cache.emit(default)
    }
}

object Storage {
    const val NAME = "snapshots.json"
    val path = pathTo(NAME, "[]")

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        coerceInputValues = true
        allowTrailingComma = true
    }

    val snapshots: KStore<List<HistoricalSnapshot>> = KStore(
        default = listOf(),
        enableCache = true,
        codec = CreatingCodec(
            path.toPath(),
            json,
        ),
    )

    private val snapshotMutex = Mutex()

    private val listenJob = atomic<Job?>(initial = null)

    @OptIn(DelicateCoroutinesApi::class)
    suspend fun startListening() = coroutineScope {
        if (listenJob.value != null) {
            return@coroutineScope
        }

        listenJob.value = GlobalScope.launch {
            launch {
                MainModel.currentMainData.collect {
                    makeSnapshot()
                }
            }

            launch {
                MainModel.currentClientData.collect {
                    makeSnapshot()
                }
            }

            launch {
                MainModel.currentCellData.collect {
                    makeSnapshot()
                }
            }

            launch {
                MainModel.currentSimData.collect {
                    makeSnapshot()
                }
            }
        }
    }

    fun stopListening() {
        listenJob.value?.cancel()
        listenJob.value = null
    }

    private suspend fun makeSnapshot() {
        snapshotMutex.withLock {
            val (mainTime, mainData) = MainModel.currentMainData.timestampedValue
            val (clientTime, clientData) = MainModel.currentClientData.timestampedValue
            val (cellTime, cellData) = MainModel.currentCellData.timestampedValue
            val (simTime, simData) = MainModel.currentSimData.timestampedValue

            val snapshotTime = maxOf(mainTime, clientTime, cellTime, simTime)

            if (snapshotTime == 0L) {
                return@withLock
            }

            val currentSnapshots = snapshots.get()?.toMutableList() ?: mutableListOf()

            val snapshot = HistoricalSnapshot(
                timeMillis = snapshotTime,
                cellData = cellData,
                clientData = clientData,
                mainData = mainData,
                simData = simData,
            )

            currentSnapshots.add(snapshot)

            snapshots.set(currentSnapshots)
        }
    }
}
