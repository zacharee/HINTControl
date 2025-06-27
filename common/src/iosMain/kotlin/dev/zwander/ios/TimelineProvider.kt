package dev.zwander.ios

import dev.zwander.common.model.GlobalModel
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.model.adapters.CellDataRoot
import dev.zwander.common.model.adapters.SignalData

object TimelineProviderUtils {
    suspend fun updateCellAndSignalData(): Pair<CellDataRoot?, SignalData?> {
        val client = GlobalModel.updateClient()
        client?.logIn(
            username = UserModel.username.value,
            password = UserModel.password.value ?: "",
            rememberCredentials = true,
        )

        return MainModel.currentCellData.value to MainModel.currentMainData.value?.signal
    }
}
