package dev.zwander.common.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.zwander.common.database.CellDataRootConverter
import dev.zwander.common.database.ClientDeviceDataConverter
import dev.zwander.common.database.MainDataConverter
import dev.zwander.common.database.SimDataRootConverter
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.ClientDeviceData
import dev.zwander.common.model.adapters.MainData
import dev.zwander.common.model.adapters.SimDataRoot
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class HistoricalSnapshot(
    val timeMillis: Long,
    @field:TypeConverters(CellDataRootConverter::class)
    val cellData: CellDataRoot? = null,
    @field:TypeConverters(ClientDeviceDataConverter::class)
    val clientData: ClientDeviceData? = null,
    @field:TypeConverters(MainDataConverter::class)
    val mainData: MainData? = null,
    @field:TypeConverters(SimDataRootConverter::class)
    val simData: SimDataRoot? = null,
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
)
