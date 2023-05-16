package dev.zwander.common.model.adapters

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CellDataRoot(
    val cell: AdvancedCellData,
)

@Serializable
data class AdvancedCellData(
    @SerialName("4g")
    val fourG: AdvancedDataLTE? = null,
    @SerialName("5g")
    val fiveG: AdvancedData5G? = null,
    val generic: GenericData? = null,
    val gps: GPSData? = null,
)

interface BaseAdvancedData {
    val bandwidth: String
    val mcc: String
    val mnc: String
    val plmn: String
    val status: Boolean
    val supportedBands: List<String>
    val sector: BaseCellData
}

@Serializable
data class AdvancedDataLTE(
    val cqi: Int,
    val earfcn: String,
    val ecgi: String,
    val pci: String,
    val tac: String,
    override val bandwidth: String,
    override val mcc: String,
    override val mnc: String,
    override val plmn: String,
    override val status: Boolean,
    override val supportedBands: List<String>,
    override val sector: CellDataLTE,
) : BaseAdvancedData

@Serializable
data class AdvancedData5G(
    val nci: Int,
    val nrarfcn: String,
    override val bandwidth: String,
    override val mcc: String,
    override val mnc: String,
    override val plmn: String,
    override val status: Boolean,
    override val supportedBands: List<String>,
    override val sector: CellData5G,
) : BaseAdvancedData

@Serializable
data class GPSData(
    val lat: Double? = null,
    val lon: Double? = null,
)
