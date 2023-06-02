@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.util

import dev.zwander.common.data.Page
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class NullSymbol {
    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
    inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}

@HiddenFromObjC
val NULL = NullSymbol()

inline fun <reified T : Any> PersistentMutableStateFlow(
    key: String,
    default: T?,
) = PersistentMutableStateFlow<T>(key, default ?: NULL, typeOf<T>())

class PersistentMutableStateFlow<T>(
    private val key: String,
    private val default: Any,
    private val typeClass: KType,
) : MutableStateFlow<T> {
    private val wrapped = MutableStateFlow(persistedValue)

    override val replayCache: List<T>
        get() = wrapped.replayCache
    override val subscriptionCount: StateFlow<Int>
        get() = wrapped.subscriptionCount
    override var value: T
        get() = wrapped.value
        set(value) {
            wrapped.value = value
            persistedValue = value
        }

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        wrapped.collect(collector)
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        return wrapped.compareAndSet(expect, update)
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        wrapped.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return wrapped.tryEmit(value)
    }

    override suspend fun emit(value: T) {
        wrapped.emit(value)
    }

    @Suppress("UNCHECKED_CAST")
    private var persistedValue: T
        get() = with (SettingsManager.settings) {
            if (default == NULL) return NULL.unbox(default)

            val castedDefault = default as? T

            NULL.unbox(
                when (castedDefault) {
                    is Int -> getIntOrNull(key) ?: castedDefault
                    is Long -> getLongOrNull(key) ?: castedDefault
                    is Float -> getFloatOrNull(key) ?: castedDefault
                    is String -> getStringOrNull(key) ?: castedDefault
                    is Boolean -> getBooleanOrNull(key) ?: castedDefault
                    is Double -> getDoubleOrNull(key) ?: castedDefault
                    is Page -> Page.pageFromKey(getStringOrNull(key) ?: castedDefault.key)
                    else -> throw IllegalStateException("Invalid type")
                }
            )
        }
        set(value) {
            with (SettingsManager.settings) {
                if (value == null) {
                    remove(key)
                    return@with
                }

                when (val castedValue = value as? T) {
                    is Int -> putInt(key, castedValue)
                    is Long -> putLong(key, castedValue)
                    is Float -> putFloat(key, castedValue)
                    is String -> putString(key, castedValue)
                    is Boolean -> putBoolean(key, castedValue)
                    is Double -> putDouble(key, castedValue)
                    is Page -> putString(key, castedValue.key)
                    else -> throw IllegalStateException("Invalid type $typeClass")
                }
            }
        }
}