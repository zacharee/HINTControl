package dev.zwander.common.util

import java.awt.Desktop
import java.net.URI
import java.net.URLEncoder

/**
 * Delegate URL launching and email sending to the platform.
 */
actual object UrlHandler {
    private val desktop = Desktop.getDesktop()

    /**
     * Launch a given URL.
     * @param url the URL to open.
     */
    actual fun launchUrl(url: String) {
        val uri = URI(url)
        desktop.browse(uri)
    }

    /**
     * Send an email.
     * @param address the address to send to.
     * @param subject the subject line (optional).
     * @param content the email body (optional).
     */
    actual fun sendEmail(address: String, subject: String?, content: String?) {
        val string = StringBuilder()
        string.append("mailto:")
        string.append(address)

        string.append("?subject=${URLEncoder.encode(subject ?: "", Charsets.UTF_8)}")
        string.append("&body=${URLEncoder.encode(content ?: "", Charsets.UTF_8)}")

        val uri = URI(string.toString())
        desktop.mail(uri)
    }
}