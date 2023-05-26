package dev.zwander.common.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KType
import kotlin.reflect.typeOf

class NullSymbol {
    @Suppress("UNCHECKED_CAST")
    inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}

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

    private var persistedValue: T
        get() = with (SettingsManager.settings) {
            if (default == NULL) return NULL.unbox(default)

            NULL.unbox(
                when (typeClass) {
                    typeOf<Int>() -> getIntOrNull(key) ?: default
                    typeOf<Long>() -> getLongOrNull(key) ?: default
                    typeOf<Float>() -> getFloatOrNull(key) ?: default
                    typeOf<String>() -> getStringOrNull(key) ?: default
                    typeOf<Boolean>() -> getBooleanOrNull(key) ?: default
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

                when(typeClass) {
                    typeOf<Int>() -> putInt(key, value.toString().toInt())
                    typeOf<Long>() -> putLong(key, value.toString().toLong())
                    typeOf<Float>() -> putFloat(key, value.toString().toFloat())
                    typeOf<String>() -> putString(key, value.toString())
                    typeOf<Boolean>() -> putBoolean(key, value.toString().toBoolean())
                    else -> throw IllegalStateException("Invalid type $typeClass")
                }
            }
        }
}