package dev.zwander.common.util

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.FileProvider
import dev.zwander.common.App
import dev.zwander.common.GradleConfig
import io.github.z4kn4fein.semver.toVersion
import io.ktor.client.HttpClient
import io.ktor.client.request.prepareRequest
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpMethod
import io.ktor.util.cio.use
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.copyTo
import korlibs.crypto.sha256
import org.kohsuke.github.GitHub
import java.io.File

actual object UpdateUtil {
    actual suspend fun checkForUpdate(): UpdateInfo? {
        return try {
            val github = GitHub.connectAnonymously()
            val repo = github.getRepository("zacharee/HINTControl")

            val latestVersion = repo.latestRelease.tagName
            val currentVersion = GradleConfig.versionName

            return if (currentVersion.toVersion() >= latestVersion.toVersion()) {
                null
            } else {
                UpdateInfo(latestVersion)
            }
        } catch (e: Throwable) {
            CrossPlatformBugsnag.notify(e)
            null
        }
    }

    @SuppressLint("RequestInstallPackagesPolicy")
    actual suspend fun installUpdate() {
        try {
            val github = GitHub.connectAnonymously()
            val repo = github.getRepository("zacharee/HINTControl")
            val assets = repo.latestRelease.listAssets().toList()

            val apk = assets.find { it.name.endsWith(".apk") } ?: return
            val downloadUrl = apk.browserDownloadUrl ?: return

            val destinationFile = File(App.instance.cacheDir, "updates/${apk.name}")
            destinationFile.delete()
            destinationFile.parentFile?.mkdirs()

            val request = HttpClient().prepareRequest {
                method = HttpMethod.Get
                url(downloadUrl)
            }

            request.execute { response ->
                val channel = response.bodyAsChannel()

                destinationFile.writeChannel().use { channel.copyTo(this) }
            }

            @Suppress("DEPRECATION")
            val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
            intent.data = FileProvider.getUriForFile(
                App.instance,
                "dev.zwander.arcadyankvd21control.fileprovider",
                destinationFile,
            )
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            App.instance.startActivity(intent)
        } catch (e: Throwable) {
            CrossPlatformBugsnag.notify(e)
        }
    }

    @Suppress("DEPRECATION")
    actual fun supported(): Boolean {
        val githubSha256 = "58997A7C334D76DCD57FAA918DDA90DA27EDA2E768AED4EB7F110D90C3196D67"
        val installSource = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            App.instance.packageManager.getInstallSourceInfo(App.instance.packageName).initiatingPackageName
        } else {
            App.instance.packageManager.getInstallerPackageName(App.instance.packageName)
        }
        val signatures = App.instance.packageManager
            .getPackageInfo(App.instance.packageName, PackageManager.GET_SIGNATURES).signatures
        val matchesSignature = signatures?.any { signature ->
            signature.toByteArray().sha256().hexUpper == githubSha256
        }

        return (matchesSignature == true && installSource != "com.android.vending") || dev.zwander.common.BuildConfig.DEBUG
    }
}