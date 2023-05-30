package dev.zwander.common.util

object NokiaConverter {
    fun convertNokiaEncryptionVersionToArcadyan(nokiaVersion: String?): String? {
        return nokiaVersion?.split("and")
            ?.joinToString("/") {
                if (it == "11i") "WPA2" else it
            }
    }

    fun convertArcadyanEncryptionVersionToNokia(arcadyanVersion: String?): String? {
        return arcadyanVersion?.split("/")
            ?.joinToString("and") {
                if (it == "WPA2") "11i" else it
            }
    }
}
