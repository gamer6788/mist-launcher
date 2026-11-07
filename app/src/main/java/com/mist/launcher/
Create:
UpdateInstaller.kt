
package com.mist.launcher

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import java.security.MessageDigest

sealed class InstallResult {
    data object Launched : InstallResult()
    data object NeedsPermission : InstallResult()
    data class Failed(val message: String) : InstallResult()
}

object UpdateInstaller {

    private const val PACKAGE_NAME = "com.mist.launcher"
    private const val MIME_TYPE = "application/vnd.android.package-archive"

    fun install(
        activity: Activity,
        apkFile: File
    ): InstallResult {

        return try {
            if (!apkFile.exists() || !apkFile.isFile || apkFile.length() == 0L) {
                throw Exception("Update APK not found")
            }

            // Read the downloaded APK information.
            val archiveInfo = getArchiveInfo(
                activity.packageManager,
                apkFile
            ) ?: throw Exception("Cannot read APK information")

            // Ensure this APK is actually MIST.
            if (archiveInfo.packageName != PACKAGE_NAME) {
                throw Exception("This APK is not MIST Launcher")
            }

            // Read the currently installed MIST application.
            val installedInfo = getInstalledInfo(
                activity.packageManager
            )

            // Compare signing certificates.
            val downloadedSignatures = getSignatures(archiveInfo)
            val installedSignatures = getSignatures(installedInfo)

            if (downloadedSignatures.isEmpty() ||
                installedSignatures.isEmpty()
            ) {
                throw Exception("Unable to verify APK signature")
            }

            val downloadedHashes = downloadedSignatures
                .map { certificateHash(it.toByteArray()) }
                .toSet()

            val installedHashes = installedSignatures
                .map { certificateHash(it.toByteArray()) }
                .toSet()

            if (downloadedHashes != installedHashes) {
                throw Exception(
                    "Security check failed: MIST signing certificate does not match"
                )
            }

            // Android 8 and newer require permission to request APK installs.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !activity.packageManager.canRequestPackageInstalls()
            ) {
                val settingsIntent = Intent(
                    android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${activity.packageName}")
                )

                activity.startActivity(settingsIntent)
                return InstallResult.NeedsPermission
            }

            // Create a secure content URI using FileProvider.
            val apkUri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                apkFile
            )

            // Hand the verified APK to Android's package installer.
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, MIME_TYPE)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            activity.startActivity(installIntent)

            InstallResult.Launched

        } catch (e: Exception) {
            InstallResult.Failed(
                e.message ?: "Unable to install MIST update"
            )
        }
    }

    private fun getArchiveInfo(
        packageManager: PackageManager,
        file: File
    ): PackageInfo? {

        val flags = if (Build.VERSION.SDK_INT >= 28) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        return if (Build.VERSION.SDK_INT >= 33) {
            packageManager.getPackageArchiveInfo(
                file.absolutePath,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageArchiveInfo(
                file.absolutePath,
                flags
            )
        }
    }

    private fun getInstalledInfo(
        packageManager: PackageManager
    ): PackageInfo {

        val flags = if (Build.VERSION.SDK_INT >= 28) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }

        return if (Build.VERSION.SDK_INT >= 33) {
            packageManager.getPackageInfo(
                PACKAGE_NAME,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(
                PACKAGE_NAME,
                flags
            )
        }
    }

    private fun getSignatures(
        info: PackageInfo
    ): Array<android.content.pm.Signature> {

        return if (Build.VERSION.SDK_INT >= 28) {
            info.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            @Suppress("DEPRECATION")
            info.signatures ?: emptyArray()
        }
    }

    private fun certificateHash(
        certificate: ByteArray
    ): String {

        return MessageDigest.getInstance("SHA-256")
            .digest(certificate)
            .joinToString("") { "%02x".format(it) }
    }
}
