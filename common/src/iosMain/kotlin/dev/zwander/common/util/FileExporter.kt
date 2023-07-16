package dev.zwander.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import platform.Foundation.NSURL
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeFolder
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalObjCRefinement

actual object FileExporter {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    @Composable
    actual fun rememberBufferedSinkCreator(): BufferedSinkCreator {
        val uiViewController = LocalUIViewController.current

        return remember {
            object : BufferedSinkCreator {
                override suspend fun invoke(fileName: String, append: Boolean): BufferedSink? {
                    val url = suspendCoroutine {
                        val controller = UIDocumentPickerViewController(
                            forExportingURLs = listOf(UTTypeFolder),
                        )
                        controller.delegate = object : NSObject(), UIDocumentPickerDelegateProtocol {
                            override fun documentPicker(
                                controller: UIDocumentPickerViewController,
                                didPickDocumentsAtURLs: List<*>
                            ) {
                                it.resume(didPickDocumentsAtURLs.firstOrNull() as? NSURL)
                            }

                            override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
                                it.resume(null)
                            }
                        }

                        uiViewController.presentViewController(
                            viewControllerToPresent = controller,
                            animated = true,
                            completion = null
                        )
                    }

                    return url?.absoluteString?.let {
                        val fileHandle = FileSystem.SYSTEM.openReadWrite(
                            file = "$it/$fileName".toPath(),
                            mustCreate = true,
                            mustExist = false
                        )

                        fileHandle.sink().buffer()
                    }
                }
            }
        }
    }
}
