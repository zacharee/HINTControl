package dev.zwander.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.coroutineScope
import okio.BufferedSink
import okio.blackholeSink
import okio.buffer
import kotlin.experimental.ExperimentalObjCRefinement

// iOS has horrible file exporting support, so just save files to the
// public documents directory that should be user-accessible.
// [
actual object FileExporter {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    @Composable
    actual fun rememberBufferedSinkCreator(): BufferedSinkCreator {
//        val uiViewController = LocalUIViewController.current

        return remember {
            object : BufferedSinkCreator {
//                private val continuation = atomic<Continuation<NSURL?>?>(null)

//                private val controller = UIDocumentPickerViewController(
//                    forExportingURLs = listOf(UTTypeFolder),
//                )

//                private val delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
//                    override fun documentPicker(
//                        controller: UIDocumentPickerViewController,
//                        didPickDocumentsAtURLs: List<*>
//                    ) {
//                        continuation.getAndSet(null)?.resume(didPickDocumentsAtURLs.firstOrNull() as? NSURL)
//                    }
//
//                    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
//                        continuation.getAndSet(null)?.resume(null)
//                    }
//                }
//
//                init {
//                    controller.delegate = delegate
//                }

                override suspend fun invoke(fileName: String, append: Boolean): BufferedSink = coroutineScope {
//                    val url = withContext(Dispatchers.Main) {
//                        suspendCoroutine {
//                            continuation.getAndSet(it)?.resume(null)
//
//                            uiViewController.presentViewController(
//                                viewControllerToPresent = controller,
//                                animated = true,
//                                completion = null,
//                            )
//                        }
//                    }
//
//                    url?.absoluteString?.let {
//                        val fileHandle = FileSystem.SYSTEM.openReadWrite(
//                            file = "$it/$fileName".toPath(),
//                            mustCreate = true,
//                            mustExist = false
//                        )
//
//                        fileHandle.sink().buffer()
//                    }

                    blackholeSink().buffer()
                }
            }
        }
    }
}
