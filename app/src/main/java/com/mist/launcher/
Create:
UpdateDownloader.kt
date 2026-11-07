
package com.mist.launcher

import android.os.Handler
import android.os.Looper
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.Executors

sealed class DownloadResult {
    data class Success(val file: File) : DownloadResult()
    data class Failed(val message: String) : DownloadResult()
}

object UpdateDownloader {

    private const val MAX_DOWNLOAD_SIZE = 200L * 1024L * 1024L

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun download(
        update: MistUpdate,
        cacheDir: File,
        onProgress: (Int) -> Unit,
        callback: (DownloadResult) -> Unit
    ) {
        executor.execute {
            var tempFile: File? = null

            val result = try {
                val url = URL(update.apkUrl)

                require(url.protocol == "https") {
                    "Insecure download URL"
                }

                require(url.host == "github.com") {
                    "Updates must come from GitHub"
                }

                val directory = File(cacheDir, "updates")

                if (!directory.exists() && !directory.mkdirs()) {
                    throw Exception("Cannot create update folder")
                }

                tempFile = File(directory, "mist-update.apk.part")

                val finalFile = File(
                    directory,
                    "mist-${update.versionName}.apk"
                )

                val connection =
                    url.openConnection() as HttpURLConnection

                try {
                    connection.connectTimeout = 15000
                    connection.readTimeout = 20000
                    connection.requestMethod = "GET"
                    connection.instanceFollowRedirects = true

                    val response = connection.responseCode

                    if (response != HttpURLConnection.HTTP_OK) {
                        throw Exception("Download failed: HTTP $response")
                    }

                    val finalHost = connection.url.host

                    if (finalHost !in setOf(
                            "github.com",
                            "objects.githubusercontent.com",
                            "release-assets.githubusercontent.com"
                        )
                    ) {
                        throw Exception("Unexpected download server")
                    }

                    val total = connection.contentLengthLong

                    if (total > MAX_DOWNLOAD_SIZE) {
                        throw Exception("APK is too large")
                    }

                    val digest = MessageDigest.getInstance("SHA-256")
                    var downloaded = 0L
                    var lastProgress = -1

                    connection.inputStream.use { input ->
                        tempFile.outputStream().use { output ->
                            val buffer = ByteArray(8192)

                            while (true) {
                                val count = input.read(buffer)
                                if (count == -1) break

                                downloaded += count

                                if (downloaded > MAX_DOWNLOAD_SIZE) {
                                    throw Exception("Download exceeds size limit")
                                }

                                digest.update(buffer, 0, count)
                                output.write(buffer, 0, count)

                                if (total > 0) {
                                    val progress =
                                        ((downloaded * 100) / total)
                                            .toInt()
                                            .coerceIn(0, 100)

                                    if (progress != lastProgress) {
                                        lastProgress = progress
                                        mainHandler.post {
                                            onProgress(progress)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (downloaded == 0L) {
                        throw Exception("Downloaded file is empty")
                    }

                    val actualHash = digest.digest()
                        .joinToString("") { "%02x".format(it) }

                    if (!actualHash.equals(update.sha256, ignoreCase = true)) {
                        throw Exception("APK checksum verification failed")
                    }

                    if (finalFile.exists()) {
                        finalFile.delete()
                    }

                    if (!tempFile.renameTo(finalFile)) {
                        throw Exception("Could not save verified APK")
                    }

                    mainHandler.post {
                        onProgress(100)
                    }

                    DownloadResult.Success(finalFile)

                } finally {
                    connection.disconnect()
                }

            } catch (e: Exception) {
                DownloadResult.Failed(
                    e.message ?: "Unable to download update"
                )
            }

            if (result is DownloadResult.Failed) {
                tempFile?.delete()
            }

            mainHandler.post {
                callback(result)
            }
        }
    }
}
