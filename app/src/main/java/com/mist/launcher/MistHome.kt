
package com.mist.launcher

import android.content.pm.ResolveInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MistHome(
    apps: List<ResolveInfo>,
    preferences: MistUserPreferences,
    onPreferencesChanged: () -> Unit,
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
    var screen by remember { mutableStateOf("home") }
    var search by remember { mutableStateOf("") }

    var clock by remember { mutableStateOf(currentTime()) }
    var date by remember { mutableStateOf(currentDate()) }

    val context = LocalContext.current

    BackHandler(
        enabled = screen != "home" || showUpdateScreen
    ) {
        if (showUpdateScreen) {
            onCloseUpdates()
        } else {
            screen = "home"
        }
    }

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

    val palette =
        MistDesignSystem.paletteFor(preferences.theme)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background)
    ) {

        SingularityAtmosphere(
            palette = palette,
            animationEnabled =
                preferences.backgroundAnimationEnabled &&
                !preferences.reducedMotion
        )

        when {

            // UPDATE SCREEN
            showUpdateScreen -> {
                Column(Modifier.fillMaxSize()) {

                    HeaderAction("CLOSE") {
                        onCloseUpdates()
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
            }

            // APPLICATION DRAWER
            screen == "drawer" -> {

                Column(
                    Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                ) {

                    ScreenHeader(
                        title = "APPLICATION LIBRARY",
                        onBack = { screen = "home" }
                    )

                    Surface(
                        color = Glass,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        BasicTextField(
                            value = search,
                            onValueChange = { search = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Silver,
                                fontSize = 15.sp
                            ),
                            decorationBox = { inner ->

                                Box(
                                    Modifier.padding(16.dp)
                                ) {

                                    if (search.isEmpty()) {
                                        Text(
                                            "Search applications",
                                            color = Muted
                                        )
                                    }

                                    inner()
                                }
                            }
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        "${filteredApps.size} APPLICATIONS",
                        color = Muted,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement =
                            Arrangement.spacedBy(12.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(20.dp),
                        modifier = Modifier.weight(1f)
                    ) {

                        items(
                            filteredApps,
                            key = {
                                it.activityInfo.packageName
                            }
                        ) { app ->

                            val name = remember(app) {
                                app.loadLabel(
                                    context.packageManager
                                ).toString()
                            }

                            val icon = remember(app) {
                                drawableBitmap(
                                    app.loadIcon(
                                        context.packageManager
                                    )
                                )
                            }

                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onOpenApp(
                                            app.activityInfo.packageName
                                        )
                                    }
                            ) {

                                Image(
                                    bitmap = icon.asImageBitmap(),
                                    contentDescription = name,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(
                                            RoundedCornerShape(14.dp)
                                        )
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    name,
                                    color = palette.textPrimary,
                                    fontSize = 10.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // APPEARANCE SETTINGS
            screen == "appearance" -> {

                SettingsPage(
                    title = "APPEARANCE",
                    onBack = { screen = "home" }
                ) {
                    MistAppearanceScreen()
                }
            }

            // EXPERIENCE SETTINGS
            screen == "experience" -> {

                SettingsPage(
                    title = "EXPERIENCE",
                    onBack = { screen = "home" }
                ) {
                    MistExperienceSettings()
                }
            }

            // HOME SCREEN
            else -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 24.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Column {

                            Text(
                                "M I S T",
                                color = palette.textPrimary,
                                fontSize = 21.sp,
                                letterSpacing = 6.sp,
                                fontWeight =
                                    androidx.compose.ui.text.font.FontWeight.Light
                            )

                            Text(
                                "BEYOND THE ORDINARY",
                                color = palette.textSecondary,
                                fontSize = 8.sp,
                                letterSpacing = 2.sp
                            )
                        }

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            SmallAction("⚙") {
                                screen = "appearance"
                            }

                            SmallAction("↻") {
                                onOpenUpdates()
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))

                    Text(
                        date.uppercase(),
                        color = palette.primary,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp
                    )

                    Text(
                        clock,
                        color = palette.textPrimary,
                        fontSize = 76.sp,
                        fontFamily =
                            androidx.compose.ui.text.font.FontFamily.Serif,
                        fontWeight =
                            androidx.compose.ui.text.font.FontWeight.Light,
                        letterSpacing = (-3).sp
                    )

                    Text(
                        "TIME EXISTS BEYOND THE VOID.",
                        color = palette.textSecondary,
                        fontSize = 9.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(30.dp))

                    Surface(
                        color = palette.surface,
                        shape = RoundedCornerShape(25.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(Modifier.padding(20.dp)) {

                            Text(
                                "THE SINGULARITY",
                                color = palette.primary,
                                fontSize = 9.sp,
                                letterSpacing = 2.sp
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                "Beyond the ordinary.",
                                color = palette.textPrimary,
                                fontSize = 23.sp,
                                fontFamily =
                                    androidx.compose.ui.text.font.FontFamily.Serif
                            )

                            Spacer(Modifier.height(7.dp))

                            Text(
                                "Enter your own atmosphere.",
                                color = palette.textSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "FAVOURITES",
                        color = palette.textSecondary,
                        fontSize = 9.sp,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(13.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                            Arrangement.spacedBy(13.dp)
                    ) {

                        apps.take(5).forEach { app ->

                            val name = remember(app) {
                                app.loadLabel(
                                    context.packageManager
                                ).toString()
                            }

                            val icon = remember(app) {
                                drawableBitmap(
                                    app.loadIcon(
                                        context.packageManager
                                    )
                                )
                            }

                            Column(
                                horizontalAlignment =
                                    Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        onOpenApp(
                                            app.activityInfo.packageName
                                        )
                                    }
                            ) {

                                Image(
                                    bitmap = icon.asImageBitmap(),
                                    contentDescription = name,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(
                                            RoundedCornerShape(14.dp)
                                        )
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    name,
                                    color = palette.textPrimary,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    HomeButton("EXPLORE APPLICATIONS") {
                        search = ""
                        onRefresh()
                        screen = "drawer"
                    }

                    Spacer(Modifier.height(10.dp))

                    HomeButton("APPEARANCE & GRAPHICS") {
                        screen = "appearance"
                    }

                    Spacer(Modifier.height(10.dp))

                    HomeButton("MIST EXPERIENCE SETTINGS") {
                        screen = "experience"
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        "M I S T  •  S I N G U L A R I T Y",
                        color = palette.textSecondary,
                        fontSize = 8.sp,
                        letterSpacing = 2.sp,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}
