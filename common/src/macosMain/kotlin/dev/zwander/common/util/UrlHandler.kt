package dev.zwander.common.util

import io.ktor.http.encodeURLQueryComponent
import platform.AppKit.NSWorkspace
import platform.Foundation.NSURL

/**
 * Delegate URL launching and email sending to the platform.
 */
actual object UrlHandler {
    /**
     * Launch a given URL.
     * @param url the URL to open.
     */
    actual fun launchUrl(url: String) {
        NSWorkspace.sharedWorkspace.openURL(
            NSURL.URLWithString(url)!!,
        )
    }

    /**
     * Send an email.
     * @param address the address to send to.
     * @param subject the subject line (optional).
     * @param content the email body (optional).
     */
    actual fun sendEmail(address: String, subject: String?, content: String?) {
        val url = buildString {
            append(address)

            val options = mutableListOf<String>()

            if (!subject.isNullOrBlank()) {
                options.add("subject=${subject.encodeURLQueryComponent()}")
            }

            if (!content.isNullOrBlank()) {
                options.add("body=${content.encodeURLQueryComponent()}")
            }

            if (options.isNotEmpty()) {
                append("?${options.joinToString("&")}")
            }
        }

        NSWorkspace.sharedWorkspace.openURL(
            NSURL.URLWithString("mailto:$url")!!,
        )
    }
}
