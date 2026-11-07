
package com.mist.launcher

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val Obsidian = Color(0xFF080D16)
private val Silver = Color(0xFFE1EAF3)
private val MistBlue = Color(0xFF9BBBD4)
private val Muted = Color(0xFF9BAFC0)
private val Glass = Color(0xB51B2B3D)

class MainActivity : ComponentActivity() {

    private fun loadApps(): List<ResolveInfo> {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        return packageManager.queryIntentActivities(intent, 0)
            .filter { it.activityInfo.packageName != packageName }
            .distinctBy { it.activityInfo.packageName }
            .sortedBy {
                it.loadLabel(packageManager).toString().lowercase()
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(8, 13, 22)
        window.navigationBarColor = android.graphics.Color.rgb(8, 13, 22)

        setContent {
            var apps by remember { mutableStateOf(loadApps()) }

            var update by remember { mutableStateOf<MistUpdate?>(null) }
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

            val currentVersion = versionInfo.versionName ?: "Unknown"

            val currentVersionCode = remember {
                if (android.os.Build.VERSION.SDK_INT >= 28) {
                    versionInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    versionInfo.versionCode
                }
            }

            // Automatic update check on startup.
            LaunchedEffect(Unit) {
                updateStatus = "Checking for updates..."

                UpdateManager.checkForUpdates(currentVersionCode) { result ->
                    when (result) {
                        is UpdateResult.Available -> {
                            update = result.update
                            updateStatus = "A new MIST update is available."
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

            MistHome(
                apps = apps,
                onRefresh = {
                    apps = loadApps()
                },
                onOpenApp = { appPackage ->
                    packageManager.getLaunchIntentForPackage(appPackage)
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

                    UpdateManager.checkForUpdates(currentVersionCode) { result ->
                        when (result) {
                            is UpdateResult.Available -> {
                                update = result.update
                                verifiedFile = null
                                updateStatus = "A new MIST update is available."
                            }

                            is UpdateResult.UpToDate -> {
                                update = null
                                verifiedFile = null
                                updateStatus = "MIST is up to date."
                            }

                            is UpdateResult.Failed -> {
                                updateStatus = result.message
                            }
                        }
                    }
                },

                onDownload = {
                    val availableUpdate = update

                    if (availableUpdate != null && !downloading) {
                        downloading = true
                        downloadProgress = 0
                        verifiedFile = null
                        updateStatus = "Downloading update..."

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
                        val result = UpdateInstaller.install(
                            activity = this@MainActivity,
                            apkFile = file
                        )

                        when (result) {
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
        }
    }
}

@Composable
private fun MistHome(
    apps: List<ResolveInfo>,
    onRefresh: () -> Unit,
    onOpenApp: (String) -> Unit,

    showUpdateScreen: Boolean,
    onOpenUpdates: () -> Unit,
    onCloseUpdates: () -> Unit,

    currentVersion: String,
    updateStatus: String,
    update: MistUpdate?,
    downloadProgress: Int,
    isDownloading: Boolean,
    isVerified: Boolean,

    onCheckUpdates: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit
) {
    var drawerOpen by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }
    var clock by remember { mutableStateOf(currentTime()) }
    var date by remember { mutableStateOf(currentDate()) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            clock = currentTime()
            date = currentDate()
            delay(30_000)
        }
    }

    val filteredApps = remember(apps, search) {
        apps.filter {
            it.loadLabel(context.packageManager)
                .toString()
                .contains(search, ignoreCase = true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Obsidian)
    ) {
        ForestAtmosphere()

        if (showUpdateScreen) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        "CLOSE",
                        color = MistBlue,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onCloseUpdates() }
                            .padding(12.dp)
                    )
                }

                MistUpdateScreen(
                    currentVersion = currentVersion,
                    status = updateStatus,
                    update = update,
                    downloadProgress = downloadProgress,
                    isDownloading = isDownloading,
                    isVerified = isVerified,
                    onCheckUpdates = onCheckUpdates,
                    onDownload = onDownload,
                    onInstall = onInstall
                )
            }

        } else if (drawerOpen) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "M I S T",
                            color = Silver,
                            fontSize = 18.sp,
                            letterSpacing = 5.sp
                        )

                        Text(
                            "APPLICATION LIBRARY",
                            color = Muted,
                            fontSize = 9.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Text(
                        "CLOSE",
                        color = MistBlue,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { drawerOpen = false }
                            .padding(12.dp)
                    )
                }

                Surface(
                    color = Glass,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicTextField(
                        value = search,
                        onValueChange = { search = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = Silver,
                            fontSize = 15.sp
                        ),
                        decorationBox = { inner ->
                            Box(Modifier.padding(17.dp)) {
                                if (search.isEmpty()) {
                                    Text(
                                        "Search applications",
                                        color = Muted,
                                        fontSize = 14.sp
                                    )
                                }
                                inner()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(22.dp))

                Text(
                    "${filteredApps.size} APPLICATIONS",
                    color = Muted,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(14.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        filteredApps,
                        key = { it.activityInfo.packageName }
                    ) { app ->

                        val name = app.loadLabel(
                            context.packageManager
                        ).toString()

                        val icon = remember(app) {
                            drawableBitmap(
                                app.loadIcon(context.packageManager)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenApp(app.activityInfo.packageName)
                                }
                        ) {
                            androidx.compose.foundation.Image(
                                bitmap = icon.asImageBitmap(),
                                contentDescription = name,
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )

                            Spacer(Modifier.height(7.dp))

                            Text(
                                name,
                                color = Silver,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 25.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "M I S T",
                            color = Silver,
                            fontSize = 18.sp,
                            letterSpacing = 5.sp,
                            fontWeight = FontWeight.Light
                        )

                        Text(
                            "S I L E N T   F O R E S T",
                            color = Muted,
                            fontSize = 8.sp,
                            letterSpacing = 2.sp
                        )
                    }

                    Text(
                        "↻",
                        color = MistBlue,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Glass)
                            .clickable { onOpenUpdates() }
                            .padding(horizontal = 11.dp, vertical = 5.dp)
                    )
                }

                Spacer(Modifier.height(48.dp))

                Text(
                    date.uppercase(),
                    color = MistBlue,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )

                Text(
                    clock,
                    color = Silver,
                    fontSize = 76.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-3).sp
                )

                Text(
                    "FIND YOUR STILLNESS.",
                    color = Muted,
                    fontSize = 9.sp,
                    letterSpacing = 3.sp
                )

                Spacer(Modifier.height(30.dp))

                Surface(
                    color = Glass,
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "THE SILENT FOREST",
                            color = MistBlue,
                            fontSize = 9.sp,
                            letterSpacing = 2.sp
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            "A world away from the noise.",
                            color = Silver,
                            fontSize = 21.sp,
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(Modifier.height(7.dp))

                        Text(
                            "Take a moment. Breathe.",
                            color = Muted,
                            fontSize = 12.sp
                        )
                    }
                }

                
                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "YOUR FAVOURITES",
                        color = Muted,
                        fontSize = 9.sp,
                        letterSpacing = 2.sp
                    )

                    Text(
                        "ALL APPS ↗",
                        color = MistBlue,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        modifier = Modifier.clickable {
                            search = ""
                            onRefresh()
                            drawerOpen = true
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    apps.take(5).forEach { app ->

                        val name = app.loadLabel(
                            context.packageManager
                        ).toString()

                        val icon = remember(app) {
                            drawableBitmap(
                                app.loadIcon(context.packageManager)
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onOpenApp(app.activityInfo.packageName)
                                }
                        ) {
                            androidx.compose.foundation.Image(
                                bitmap = icon.asImageBitmap(),
                                contentDescription = name,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(15.dp))
                            )

                            Spacer(Modifier.height(6.dp))

                            Text(
                                name,
                                color = Silver,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(22.dp))

                Surface(
                    color = Color(0xD927394B),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            search = ""
                            onRefresh()
                            drawerOpen = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(17.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "⌕    EXPLORE APPLICATIONS",
                            color = Silver,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Spacer(Modifier.height(15.dp))

                Text(
                    "M I S T   •   F U S I O N",
                    color = Muted,
                    fontSize = 8.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

private fun currentTime(): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

private fun currentDate(): String =
    SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())

private fun drawableBitmap(drawable: Drawable): Bitmap {
    val width = drawable.intrinsicWidth.coerceAtLeast(1)
    val height = drawable.intrinsicHeight.coerceAtLeast(1)

    val bitmap = Bitmap.createBitmap(
        width.coerceAtMost(192),
        height.coerceAtMost(192),
        Bitmap.Config.ARGB_8888
    )

    val canvas = AndroidCanvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

@Composable
private fun ForestAtmosphere() {
    Canvas(Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF080D16),
                    Color(0xFF101D2A),
                    Color(0xFF09131D)
                )
            )
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x405C829F),
                    Color.Transparent
                ),
                center = androidx.compose.ui.geometry.Offset(
                    w * 0.72f,
                    h * 0.45f
                ),
                radius = w * 0.65f
            ),
            radius = w * 0.65f,
            center = androidx.compose.ui.geometry.Offset(
                w * 0.72f,
                h * 0.45f
            )
        )

        // Distant forest
        for (i in 0..9) {
            val x = w * i / 9f
            val base = h * (0.78f + (i % 3) * 0.025f)
            val treeHeight = h * (0.16f + (i % 4) * 0.025f)

            val tree = Path().apply {
                moveTo(x, base - treeHeight)
                lineTo(x - w * 0.065f, base)
                lineTo(x + w * 0.065f, base)
                close()
            }

            drawPath(tree, Color(0xFF142332))
        }

        // Foreground forest
        for (i in 0..7) {
            val x = w * i / 7f
            val base = h * 0.91f
            val treeHeight = h * (0.23f + (i % 3) * 0.035f)

            val tree = Path().apply {
                moveTo(x, base - treeHeight)
                lineTo(x - w * 0.085f, base)
                lineTo(x + w * 0.085f, base)
                close()
            }

            drawPath(tree, Color(0xFF0A1520))
        }

        // Static mist
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x183D6079),
                    Color(0x080B1723),
                    Color.Transparent
                ),
                startY = h * 0.65f,
                endY = h * 0.95f
            )
        )
    }
}




