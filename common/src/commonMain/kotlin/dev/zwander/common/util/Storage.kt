package dev.zwander.common.util

import dev.zwander.common.data.HistoricalSnapshot
import dev.zwander.common.model.MainModel
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.extensions.listStoreOf
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

expect fun pathTo(subPath: String, startingTag: String): String

object Storage {
    const val name = "snapshots.json"
    val path = pathTo(name, "[]")

    @OptIn(ExperimentalSerializationApi::class)
    val snapshots: KStore<List<HistoricalSnapshot>> = listStoreOf(
        file = path.toPath(),
        default = listOf(),
        enableCache = false,
        json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            coerceInputValues = true
            allowTrailingComma = true
        },
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

            val snapshot = HistoricalSnapshot(
                timeMillis = snapshotTime,
                cellData = cellData,
                clientData = clientData,
                mainData = mainData,
                simData = simData,
            )

            snapshots.update {
                (it ?: listOf()) + snapshot
            }
        }
    }
}
