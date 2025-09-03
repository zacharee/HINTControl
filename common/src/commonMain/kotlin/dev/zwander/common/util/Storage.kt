@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")
package dev.zwander.common.util

import dev.zwander.common.data.HistoricalSnapshot
import dev.zwander.common.database.Database
import dev.zwander.common.database.getRoomDatabase
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SimDataRoot
import io.github.xxfast.kstore.Codec
import io.github.xxfast.kstore.file.FileCodec
import io.github.xxfast.kstore.utils.StoreDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

expect fun pathTo(subPath: String, startingTag: String): String

class CreatingCodec<T : @Serializable Any>(
    private val file: Path,
    private val json: Json,
    private val serializer: KSerializer<T>,
    private val wrappedCodec: FileCodec<T> = FileCodec(file, Path("$file.temp"), json, serializer),
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
            val retries = 3
            CrossPlatformBugsnag.notify(IllegalStateException("Unable to decode JSON from file, retrying $retries times.", e))

            for (i in 0 until retries) {
                try {
                    return wrappedCodec.decode()
                } catch (_: Throwable) {}
            }

            CrossPlatformBugsnag.notify(IllegalStateException("Unable to decode JSON after $retries tries. Attempting to delete stored JSON file."))

            try {
                encode(null)
            } catch (e2: Throwable) {
                CrossPlatformBugsnag.notify(IllegalStateException("Unable to delete stored data after failure to read data.", e2))
            }

            null
        }
    }

    override suspend fun encode(value: T?) {
        wrappedCodec.encode(value)
    }

    private fun ensureCreated() {
        file.parent?.let {
            if (!SystemFileSystem.exists(it)) {
                SystemFileSystem.createDirectories(it)
            }
        }
    }
}

class KStore<T : @Serializable Any>(
    private val default: T? = null,
    private val enableCache: Boolean = true,
    private val codec: Codec<T>,
    private val lock: Mutex = Mutex(),
) {
    private val cache: MutableStateFlow<T?> = MutableStateFlow(default)

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

    @Deprecated("Use [snapshotsDb] instead.")
    val snapshots: KStore<List<HistoricalSnapshot>> = KStore(
        default = listOf(),
        enableCache = false,
        codec = CreatingCodec(
            Path(path),
            json,
        ),
    )

    val snapshotsDb: Database by lazy {
        getRoomDatabase()
    }

    suspend fun makeSnapshot(
        snapshotTime: Long,
        mainData: MainData? = null,
        clientData: ClientDeviceData? = null,
        cellData: CellDataRoot? = null,
        simData: SimDataRoot? = null,
    ) {
        if (SettingsModel.recordSnapshots.value) {
            val dao = snapshotsDb.getDao()

            dao.insert(
                HistoricalSnapshot(
                    timeMillis = snapshotTime,
                    cellData = cellData,
                    clientData = clientData,
                    mainData = mainData,
                    simData = simData,
                ),
            )
        }
    }

    suspend fun migrateSnapshotsIfNeeded() {
        val snapshotsSnapshot = snapshots.get()

        if (!snapshotsSnapshot.isNullOrEmpty()) {
            snapshotsDb.getDao().insert(snapshotsSnapshot)

            snapshots.delete()
        }
    }
}
