@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

package dev.zwander.common.util

import korlibs.time.DateTime
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimestampedMutableStateFlow<T>(
    initialState: T,
) : MutableStateFlow<T> {
    private val wrapped = MutableStateFlow(initialState)

    private val _lastUpdateTime = atomic(0L)
    val lastUpdateTime: Long
        get() = _lastUpdateTime.value

    override val replayCache: List<T>
        get() = wrapped.replayCache
    override val subscriptionCount: StateFlow<Int>
        get() = wrapped.subscriptionCount

    override var value: T
        get() = wrapped.value
        set(value) {
            wrapped.value = value
            _lastUpdateTime.value = DateTime.nowUnixMillisLong()
        }

    val timestampedValue: Pair<Long, T>
        get() = _lastUpdateTime.value to value

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
}
