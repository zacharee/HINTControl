package dev.zwander.common.util

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual object FileExporter {
    @SuppressLint("Recycle")
    @Composable
    actual fun rememberBufferedSinkCreator(): BufferedSinkCreator {
        val continuations = ConcurrentLinkedQueue<Continuation<Uri?>>()

        val context = LocalContext.current
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
                continuations.poll()?.resume(it)
            }

        return remember {
            object : BufferedSinkCreator {
                override suspend fun invoke(fileName: String, append: Boolean): BufferedSink? {
                    val uri = suspendCoroutine {
                        continuations.offer(it)
                        launcher.launch(fileName)
                    }

                    return uri?.let {
                        context.contentResolver.openOutputStream(
                            uri,
                            if (append) "wa" else "w",
                        )?.sink()?.buffer()
                    }
                }
            }
        }
    }
}