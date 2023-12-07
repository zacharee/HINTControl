package dev.zwander.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Velocity
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
@Composable
fun Modifier.keyboardDismissalNestedScrolling(
    wrappedConnection: NestedScrollConnection? = null,
): Modifier {
    val focusManager = LocalFocusManager.current
    val connection = remember {
        object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity): Velocity {
                if (available.y > 0) {
                    focusManager.clearFocus(true)
                }

                return wrappedConnection?.onPreFling(available)
                    ?: super.onPreFling(available)
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return wrappedConnection?.onPostFling(consumed, available)
                    ?: super.onPostFling(consumed, available)
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return wrappedConnection?.onPostScroll(consumed, available, source)
                    ?: super.onPostScroll(consumed, available, source)
            }

            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return wrappedConnection?.onPreScroll(available, source)
                    ?: super.onPreScroll(available, source)
            }

            override fun toString(): String {
                return wrappedConnection?.toString() ?: super.toString()
            }

            override fun hashCode(): Int {
                return wrappedConnection?.hashCode() ?: super.hashCode()
            }

            override fun equals(other: Any?): Boolean {
                return wrappedConnection?.equals(other) ?: super.equals(other)
            }
        }
    }

    return nestedScroll(connection)
}
