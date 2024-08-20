package dev.zwander.common.util

import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataUsingEncoding

actual fun pathTo(subPath: String, startingTag: String): String {
    val cachesUrl = NSURL.fileURLWithPath(NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory,
        NSUserDomainMask,
        true,
    ).firstOrNull().toString())

    val fileUrl = cachesUrl.URLByAppendingPathComponent(subPath)!!

    val fileManager = NSFileManager.defaultManager

    if (!fileManager.fileExistsAtPath(fileUrl.path!!)) {
        @Suppress("CAST_NEVER_SUCCEEDS")
        fileManager.createFileAtPath(
            fileUrl.path!!,
            (startingTag as NSString).dataUsingEncoding(NSUTF8StringEncoding),
            null
        )
    }

    return "${fileUrl.path}"
}
