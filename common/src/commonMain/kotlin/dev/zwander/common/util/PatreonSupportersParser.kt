package dev.zwander.common.util

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.utils.*
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Serializable
data class SupporterInfo(
    val name: String,
    val link: String
)

class PatreonSupportersParser private constructor() {
    companion object {
        @Suppress("VARIABLE_IN_SINGLETON_WITHOUT_THREAD_LOCAL")
        private var instance: PatreonSupportersParser? = null

        fun getInstance(): PatreonSupportersParser {
            return instance ?: PatreonSupportersParser().also {
                instance = it
            }
        }
    }

    @OptIn(InternalAPI::class)
    suspend fun parseSupporters(): List<SupporterInfo> {
        val supportersString = StringBuilder()

        withContext(Dispatchers.clientDispatcher(5, "Supporters")) {
            try {
                val statement = HttpClient().use {
                    it.get {
                        url("https://raw.githubusercontent.com/zacharee/PatreonSupportersRetrieval/master/app/src/main/assets/supporters.json")
                    }
                }

                supportersString.append(statement.bodyAsText())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return try {
            Json.decodeFromString(ListSerializer(SupporterInfo.serializer()), supportersString.toString())
        } catch (e: Exception) {
            e.printStackTrace()
            listOf()
        }
    }
}