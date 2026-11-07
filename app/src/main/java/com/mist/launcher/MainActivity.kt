


package com.mist.launcher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.io.File

class MainActivity : ComponentActivity() {

    private fun loadApps(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return packageManager.queryIntentActivities(intent, 0)
            .filter {
                it.activityInfo.packageName != packageName
            }
            .distinctBy {
                it.activityInfo.packageName
            }
            .sortedBy {
                it.loadLabel(packageManager)
                    .toString()
                    .lowercase()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor =
            android.graphics.Color.rgb(8, 7, 18)

        window.navigationBarColor =
            android.graphics.Color.rgb(8, 7, 18)

        setContent {

            var preferences by remember {
                mutableStateOf(
                    MistPreferences.load(this@MainActivity)
                )
            }

            var apps by remember {
                mutableStateOf(loadApps())
            }

            var introVisible by remember {
                mutableStateOf(preferences.introEnabled)
            }

            var update by remember {
                mutableStateOf<MistUpdate?>(null)
            }

            var updateStatus by remember {
                mutableStateOf("Preparing update service...")
            }

            var downloadProgress by remember {
                mutableIntStateOf(0)
            }

            var downloading by remember {
                mutableStateOf(false)
            }

            var verifiedFile by remember {
                mutableStateOf<File?>(null)
            }

            var updateScreen by remember {
                mutableStateOf(false)
            }

            val versionInfo = remember {
                packageManager.getPackageInfo(packageName, 0)
            }

            val currentVersion =
                versionInfo.versionName ?: "Unknown"

            val currentVersionCode = remember {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    versionInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    versionInfo.versionCode
                }
            }

            LaunchedEffect(Unit) {
                updateStatus = "Checking for updates..."

                UpdateManager.checkForUpdates(
                    currentVersionCode
                ) { result ->

                    when (result) {
                        is UpdateResult.Available -> {
                            update = result.update
                            updateStatus =
                                "A new MIST update is available."
                        }

                        is UpdateResult.UpToDate -> {
                            update = null
                            updateStatus = "MIST is up to date."
                        }

                        is UpdateResult.Failed -> {
                            updateStatus = result.message
                        }
                    }
                }
            }

            MistThemeProvider(theme = preferences.theme) {

                Box(Modifier.fillMaxSize()) {

                    MistHome(
                        apps = apps,
                        preferences = preferences,

                        onPreferencesChanged = {
                            preferences =
                                MistPreferences.load(
                                    this@MainActivity
                                )
                        },

                        onRefresh = {
                            apps = loadApps()
                        },

                        onOpenApp = { appPackage ->
                            packageManager
                                .getLaunchIntentForPackage(appPackage)
                                ?.let { startActivity(it) }
                        },

                        showUpdateScreen = updateScreen,

                        onOpenUpdates = {
                            updateScreen = true
                        },

                        onCloseUpdates = {
                            updateScreen = false
                        },

                        currentVersion = currentVersion,
                        updateStatus = updateStatus,
                        update = update,
                        downloadProgress = downloadProgress,
                        isDownloading = downloading,
                        isVerified = verifiedFile != null,

                        onCheckUpdates = {
                            updateStatus = "Checking for updates..."

                            UpdateManager.checkForUpdates(
                                currentVersionCode
                            ) { result ->

                                when (result) {
                                    is UpdateResult.Available -> {
                                        update = result.update
                                        verifiedFile = null
                                        updateStatus =
                                            "A new MIST update is available."
                                    }

                                    is UpdateResult.UpToDate -> {
                                        update = null
                                        verifiedFile = null
                                        updateStatus =
                                            "MIST is up to date."
                                    }

                                    is UpdateResult.Failed -> {
                                        updateStatus = result.message
                                    }
                                }
                            }
                        },

                        onDownload = {
                            val availableUpdate = update

                            if (
                                availableUpdate != null &&
                                !downloading
                            ) {
                                downloading = true
                                downloadProgress = 0
                                verifiedFile = null
                                updateStatus =
                                    "Downloading update..."

                                UpdateDownloader.download(
                                    update = availableUpdate,
                                    cacheDir = cacheDir,

                                    onProgress = { progress ->
                                        downloadProgress = progress
                                    },

                                    callback = { result ->
                                        downloading = false

                                        when (result) {
                                            is DownloadResult.Success -> {
                                                verifiedFile = result.file
                                                updateStatus =
                                                    "Download complete. Checksum verified."
                                            }

                                            is DownloadResult.Failed -> {
                                                verifiedFile = null
                                                updateStatus = result.message
                                            }
                                        }
                                    }
                                )
                            }
                        },

                        onInstall = {
                            val file = verifiedFile

                            if (file != null) {
                                when (
                                    val result =
                                        UpdateInstaller.install(
                                            activity = this@MainActivity,
                                            apkFile = file
                                        )
                                ) {
                                    is InstallResult.Launched -> {
                                        updateStatus =
                                            "Android installer opened. Approve the update."
                                    }

                                    is InstallResult.NeedsPermission -> {
                                        updateStatus =
                                            "Allow MIST to install updates, then tap Install again."
                                    }

                                    is InstallResult.Failed -> {
                                        updateStatus = result.message
                                    }
                                }
                            }
                        }
                    )

                    if (introVisible) {
                        SingularityIntro(
                            onFinished = {
                                introVisible = false
                            },
                            onSkip = {
                                introVisible = false
                            }
                        )
                    }
                }
            }
        }
    }
}
