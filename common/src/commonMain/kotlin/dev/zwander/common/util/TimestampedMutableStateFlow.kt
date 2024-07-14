@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

package dev.zwander.common.util

import korlibs.time.DateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TimestampedMutableStateFlow<T>(
    initialState: T,
) : MutableStateFlow<T> {
    val wrapped = MutableStateFlow(0L to initialState)

    override val replayCache: List<T>
        get() = wrapped.replayCache.map { it.second }
    override val subscriptionCount: StateFlow<Int>
        get() = wrapped.subscriptionCount

    override var value: T
        get() = wrapped.value.second
        set(value) {
            wrapped.value = DateTime.nowUnixMillisLong() to value
        }

    val timestampedValue: Pair<Long, T>
        get() = wrapped.value

    override suspend fun collect(collector: FlowCollector<T>): Nothing {
        wrapped.collect {
            collector.emit(it.second)
        }
    }

    override fun compareAndSet(expect: T, update: T): Boolean {
        return wrapped.compareAndSet(
            wrapped.value.first to expect,
            DateTime.nowUnixMillisLong() to update,
        )
    }

    @ExperimentalCoroutinesApi
    override fun resetReplayCache() {
        wrapped.resetReplayCache()
    }

    override fun tryEmit(value: T): Boolean {
        return wrapped.tryEmit(DateTime.nowUnixMillisLong() to value)
    }

    override suspend fun emit(value: T) {
        wrapped.emit(DateTime.nowUnixMillisLong() to value)
    }
}
