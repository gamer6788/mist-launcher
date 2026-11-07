
package com.mist.launcher

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

data class MistUpdate(
    val versionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val sha256: String,
    val releaseNotes: String
)

sealed class UpdateResult {
    data class Available(val update: MistUpdate) : UpdateResult()
    data object UpToDate : UpdateResult()
    data class Failed(val message: String) : UpdateResult()
}

object UpdateManager {

    private const val MANIFEST_URL =
        "https://raw.githubusercontent.com/gamer6788/mist-releases/main/latest.json"

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun checkForUpdates(
        currentVersionCode: Int,
        callback: (UpdateResult) -> Unit
    ) {
        executor.execute {
            val result = try {
                val connection =
                    URL(MANIFEST_URL).openConnection() as HttpURLConnection

                try {
                    connection.connectTimeout = 10000
                    connection.readTimeout = 10000
                    connection.requestMethod = "GET"
                    connection.setRequestProperty(
                        "Accept",
                        "application/json"
                    )

                    val responseCode = connection.responseCode

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        throw Exception("Server returned HTTP $responseCode")
                    }

                    val response = connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                    val json = JSONObject(response)

                    val versionCode = json.getInt("versionCode")
                    val versionName = json.getString("versionName")
                    val apkUrl = json.getString("apkUrl")
                    val sha256 = json.getString("sha256")
                    val releaseNotes =
                        json.optString("releaseNotes", "MIST update available")

                    if (versionCode <= 0 ||
                        versionName.isBlank() ||
                        !apkUrl.startsWith("https://") ||
                        !apkUrl.contains("github.com/") ||
                        !sha256.matches(Regex("(?i)[a-f0-9]{64}"))
                    ) {
                        throw Exception("Invalid update information")
                    }

                    if (versionCode > currentVersionCode) {
                        UpdateResult.Available(
                            MistUpdate(
                                versionCode,
                                versionName,
                                apkUrl,
                                sha256.lowercase(),
                                releaseNotes
                            )
                        )
                    } else {
                        UpdateResult.UpToDate
                    }
                } finally {
                    connection.disconnect()
                }
            } catch (e: Exception) {
                UpdateResult.Failed(
                    e.message ?: "Unable to check for updates"
                )
            }

            mainHandler.post {
                callback(result)
            }
        }
    }
}
