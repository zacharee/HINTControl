package dev.zwander.common.model.adapters.nokia

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfoStatus(
    @SerialName("device_app_status")
    val deviceAppStatus: List<DeviceAppStatus>? = null,
    @SerialName("device_cfg")
    val deviceConfig: List<DeviceConfigItem>? = null,
    @SerialName("bluetooth_status")
    val bluetoothStatus: List<BluetoothStatus>? = null,
    @SerialName("unread_sms")
    val unreadSms: List<UnreadSms>? = null,
)

@Serializable
data class DeviceAppStatus(
    @SerialName("ManufacturerOUI")
    val manufacturer: String? = null,
    @SerialName("ProductClass")
    val productClass: String? = null,
    @SerialName("SerialNumber")
    val serialNumber: String? = null,
    @SerialName("HardwareVersion")
    val hardwareVersion: String? = null,
    @SerialName("SoftwareVersion")
    val softwareVersion: String? = null,
    @SerialName("Description")
    val description: String? = null,
    @SerialName("X_ALU_COM_AllowedForwarding")
    val forwardingAllowed: Int? = null,
    @SerialName("UpTime")
    val upTime: Long? = null,
    @SerialName("lot_number")
    val lotNumber: String? = null,
)

@Serializable
data class DeviceConfigItem(
    @SerialName("_oid")
    val oid: Int? = null,
    @SerialName("HostName")
    val hostName: String? = null,
    @SerialName("IPAddress")
    val ipAddress: String? = null,
    @SerialName("MACAddress")
    val macAddress: String? = null,
    @SerialName("AddressSource")
    val addressSource: String? = null,
    @SerialName("Active")
    val active: Int? = null,
    @SerialName("InterfaceType")
    val interfaceType: String? = null,
    @SerialName("LeaseTimeRemaining")
    val leaseTimeRemaining: Long? = null,
    @SerialName("X_ALU_COM_LastActiveTime")
    val lastActiveTime: String? = null,
)

@Serializable
data class BluetoothStatus(
    @SerialName("Enable")
    val enable: Int? = null,
    @SerialName("Status")
    val status: String? = null,
)

@Serializable
data class UnreadSms(
    @SerialName("UnreadSMS")
    val unreadSms: Int? = null,
)
