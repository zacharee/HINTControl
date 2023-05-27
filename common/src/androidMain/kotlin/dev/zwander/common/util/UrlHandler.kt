package dev.zwander.common.util

import dev.zwander.common.App

/**
 * Delegate URL launching and email sending to the platform.
 */
actual object UrlHandler {
    /**
     * Launch a given URL.
     * @param url the URL to open.
     */
    actual fun launchUrl(url: String) {
        App.instance?.launchUrl(url)
    }

    /**
     * Send an email.
     * @param address the address to send to.
     * @param subject the subject line (optional).
     * @param content the email body (optional).
     */
    actual fun sendEmail(address: String, subject: String?, content: String?) {
        App.instance?.launchEmail(address, subject, content)
    }
}