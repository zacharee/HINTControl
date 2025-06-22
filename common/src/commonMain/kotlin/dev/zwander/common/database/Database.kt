package dev.zwander.common.database

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverter
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.zwander.common.data.HistoricalSnapshot
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SimDataRoot
import dev.zwander.common.util.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

@Database(entities = [HistoricalSnapshot::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class Database : RoomDatabase() {
    abstract fun getDao(): SnapshotDao
}

@Dao
interface SnapshotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg items: HistoricalSnapshot)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(items: List<HistoricalSnapshot>)

    @Query("DELETE FROM HistoricalSnapshot WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM HistoricalSnapshot")
    suspend fun deleteAll()

    @Query("SELECT id from HistoricalSnapshot ORDER BY timeMillis ASC")
    suspend fun getIds(): List<Long>

    @Query("SELECT * FROM HistoricalSnapshot WHERE id = :id")
    suspend fun getById(id: Long): HistoricalSnapshot

    @Query("SELECT count(*) FROM HistoricalSnapshot")
    fun countAsFlow(): Flow<Int>

    @Query("SELECT * FROM HistoricalSnapshot WHERE timeMillis >= :start AND timeMillis <= :end ORDER BY timeMillis ASC")
    fun getForPeriod(start: Long, end: Long): Flow<List<HistoricalSnapshot>>

    @Query("SELECT * FROM HistoricalSnapshot WHERE timeMillis >= :since ORDER BY timeMillis ASC")
    suspend fun getSince(since: Long): List<HistoricalSnapshot>

    @Query("SELECT * FROM HistoricalSnapshot WHERE timeMillis in (" +
            "SELECT timeMillis FROM HistoricalSnapshot ORDER BY timeMillis DESC LIMIT :count" +
    ") ORDER BY timeMillis ASC")
    fun getLast(count: Long): Flow<List<HistoricalSnapshot>>
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<dev.zwander.common.database.Database> {
    override fun initialize(): dev.zwander.common.database.Database
}

expect fun getDatabaseBuilder(): RoomDatabase.Builder<dev.zwander.common.database.Database>

fun getRoomDatabase(): dev.zwander.common.database.Database {
    return getDatabaseBuilder()
        .fallbackToDestructiveMigrationOnDowngrade(false)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

class CellDataRootConverter {
    @TypeConverter
    fun fromCellDataRoot(root: CellDataRoot?): String? {
        return root?.let { Storage.json.encodeToString(it) }
    }

    @TypeConverter
    fun toCellDataRoot(root: String?): CellDataRoot? {
        return root?.let { Storage.json.decodeFromString(it) }
    }
}

class ClientDeviceDataConverter {
    @TypeConverter
    fun fromClientDeviceData(data: ClientDeviceData?): String? {
        return data?.let { Storage.json.encodeToString(it) }
    }

    @TypeConverter
    fun toClientDeviceData(data: String?): ClientDeviceData? {
        return data?.let { Storage.json.decodeFromString(it) }
    }
}

class MainDataConverter {
    @TypeConverter
    fun fromMainData(data: MainData?): String? {
        return data?.let { Storage.json.encodeToString(it) }
    }

    @TypeConverter
    fun toMainData(data: String?): MainData? {
        return data?.let { Storage.json.decodeFromString(it) }
    }
}

class SimDataRootConverter {
    @TypeConverter
    fun fromSimDataRoot(root: SimDataRoot?): String? {
        return root?.let { Storage.json.encodeToString(it) }
    }

    @TypeConverter
    fun toSimDataRoot(root: String?): SimDataRoot? {
        return root?.let { Storage.json.decodeFromString(it) }
    }
}
