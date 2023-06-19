package dev.zwander.common.util

import dev.zwander.common.model.UserModel
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.request
import io.ktor.util.toMap
import korlibs.io.util.quote

object HttpUtils {
    private val urlRegex by lazy {
        Regex(Regex.quote(
            " <>\"#%{}\\^~[]`;/?:@=&"
        ))
    }

    suspend fun HttpResponse.formatForReport(): Map<String, String> {
        val map = mutableMapOf<String, String>()

        map["status"] = this.status.value.toString().stripPassword()
        map["headers"] = this.headers.toMap().map {
            "${it.key}=${it.value}"
        }.joinToString(",", "[", "]").stripPassword()
        map["requestHeaders"] = this.request.headers.toMap().map {
            "${it.key}=${it.value}"
        }.joinToString(",", "[", "]").stripPassword()
        map["requestBody"] = this.request.content.toByteArray().decodeToString().stripPassword()
        map["requestUrl"] = this.request.url.toString().stripPassword()
        map["requestMethod"] = this.request.method.value

        return map
    }

    // Don't send passwords in error reports.
    private fun String.stripPassword(): String {
        return if (!this.contains("pswd") && !this.contains("password")) {
            this
        } else {
            UserModel.password.value?.let {
                // Check for "|" manually since some versions of Android don't like it being
                // used in regex.
                this.replace(it, "***") + (if (it.matches(urlRegex) || it.contains('|')) {
                    (urlRegex.findAll(it) + if (it.contains('|')) listOf("|") else listOf())
                        .joinToString(",", "[", "]")
                } else {
                    ""
                })
            } ?: this
        }
    }
}
