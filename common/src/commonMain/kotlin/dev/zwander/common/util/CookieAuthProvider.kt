package dev.zwander.common.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.AuthProvider
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.HttpAuthHeader
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CompletableDeferred
import kotlinx.serialization.Serializable

fun Auth.cookies(block: CookieAuthConfig.() -> Unit) {
    with(CookieAuthConfig().apply(block)) {
        this@cookies.providers.add(CookieAuthProvider(_refreshCookies, _loadCookies, _sendWithoutRequest))
    }
}

@Serializable
data class Cookies(
    val sid: String? = null,
    val lsid: String? = null,
)

data class RefreshCookiesParams(
    val client: HttpClient,
    val response: HttpResponse,
    val oldCookies: Cookies?,
) {
    fun HttpRequestBuilder.markAsRefreshRequest() {
        attributes.put(Auth.AuthCircuitBreaker, Unit)
    }
}

class AuthCookiesHolder<T>(
    private val loadCookies: suspend () -> T?,
) {
    private val refreshCookiesDeferred = atomic<CompletableDeferred<T?>?>(null)
    private val loadCookiesDeferred = atomic<CompletableDeferred<T?>?>(null)

    internal fun clearCookies() {
        refreshCookiesDeferred.value = null
        loadCookiesDeferred.value = null
    }

    internal suspend fun loadCookies(): T? {
        var deferred: CompletableDeferred<T?>?

        lateinit var newDeferred: CompletableDeferred<T?>

        while (true) {
            deferred = loadCookiesDeferred.value
            val newValue = deferred ?: CompletableDeferred()
            if (loadCookiesDeferred.compareAndSet(deferred, newValue)) {
                newDeferred = newValue
                break
            }
        }

        if (deferred != null) {
            return deferred.await()
        }

        val newCookies = loadCookies()

        newDeferred.complete(newCookies)

        return newCookies
    }

    internal suspend fun setCookie(block: suspend () -> T?): T? {
        var deferred: CompletableDeferred<T?>?
        lateinit var newDeferred: CompletableDeferred<T?>
        while (true) {
            deferred = refreshCookiesDeferred.value
            val newValue = deferred ?: CompletableDeferred()
            if (refreshCookiesDeferred.compareAndSet(deferred, newValue)) {
                newDeferred = newValue
                break
            }
        }

        val newCookie = if (deferred == null) {
            val newTokens = block()

            newDeferred.complete(newTokens)
            refreshCookiesDeferred.value = null
            newTokens
        } else {
            deferred.await()
        }
        loadCookiesDeferred.value = CompletableDeferred(newCookie)
        return newCookie
    }
}

class CookieAuthConfig {
    internal var _refreshCookies: suspend RefreshCookiesParams.() -> Cookies? = { null }
    internal var _loadCookies: suspend () -> Cookies? = { null }
    internal var _sendWithoutRequest: (HttpRequestBuilder) -> Boolean = { true }

    fun refreshCookies(block: suspend RefreshCookiesParams.() -> Cookies?) {
        _refreshCookies = block
    }

    fun loadCookies(block: suspend () -> Cookies?) {
        _loadCookies = block
    }

    fun sendWithoutRequewst(block: (HttpRequestBuilder) -> Boolean) {
        _sendWithoutRequest = block
    }
}

class CookieAuthProvider(
    private val refreshCookies: suspend RefreshCookiesParams.() -> Cookies?,
    loadCookies: suspend () -> Cookies?,
    private val sendWithoutRequestCallback: (HttpRequestBuilder) -> Boolean = { true },
) : AuthProvider {
    @Suppress("OverridingDeprecatedMember", "DeprecatedCallableAddReplaceWith")
    @Deprecated("Please use sendWithoutRequest function instead")
    override val sendWithoutRequest: Boolean
        get() = error("Deprecated")

    private val cookiesHolder = AuthCookiesHolder(loadCookies)

    override fun sendWithoutRequest(request: HttpRequestBuilder): Boolean = sendWithoutRequestCallback(request)

    override fun isApplicable(auth: HttpAuthHeader): Boolean {
        return false
    }

    override suspend fun addRequestHeaders(
        request: HttpRequestBuilder,
        authHeader: HttpAuthHeader?
    ) {
        val cookies = cookiesHolder.loadCookies() ?: return

        request.headers {
            if (contains(HttpHeaders.Cookie)) {
                remove(HttpHeaders.Cookie)
            }

            append(HttpHeaders.Cookie, "${cookies.sid}; ${cookies.lsid}; err=deleted")
        }
    }

    override suspend fun refreshToken(response: HttpResponse): Boolean {
        val newCookies = cookiesHolder.setCookie {
            refreshCookies(RefreshCookiesParams(response.call.client, response, cookiesHolder.loadCookies()))
        }

        return newCookies != null
    }

    fun clearCookies() {
        cookiesHolder.clearCookies()
    }
}
