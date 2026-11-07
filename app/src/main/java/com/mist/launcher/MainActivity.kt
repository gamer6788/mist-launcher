
            


package com.mist.launcher

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.foundation.Canvas
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val MistBackground = Color(0xFF08111C)
private val MistPanel = Color(0xCC1A2938)
private val MistText = Color(0xFFE8EEF5)
private val MistMuted = Color(0xFFA7B7C8)
private val MistSilver = Color(0xFFC5D4E3)

class MainActivity : ComponentActivity() {

    private fun getApps(): List<ResolveInfo> {
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

        window.statusBarColor = android.graphics.Color.rgb(8, 17, 28)
        window.navigationBarColor = android.graphics.Color.rgb(8, 17, 28)

        setContent {
            var apps by remember { mutableStateOf(getApps()) }

            MistLauncher(
                apps = apps,
                onOpenApp = { packageName ->
                    val launchIntent =
                        packageManager.getLaunchIntentForPackage(packageName)

                    launchIntent?.let { startActivity(it) }
                }
            )
        }
    }
}

@Composable
fun MistLauncher(
    apps: List<ResolveInfo>,
    onOpenApp: (String) -> Unit
) {
    var showDrawer by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    var clock by remember {
        mutableStateOf(
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        )
    }

    var date by remember {
        mutableStateOf(
            SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
                .format(Date())
        )
    }

    LaunchedEffect(Unit) {
        while (true) {
            clock = SimpleDateFormat(
                "HH:mm", Locale.getDefault()
            ).format(Date())

            date = SimpleDateFormat(
                "EEEE, d MMMM", Locale.getDefault()
            ).format(Date())

            delay(30_000)
        }
    }

    val context = LocalContext.current

    val filteredApps = apps.filter {
        it.loadLabel(context.packageManager)
            .toString()
            .contains(search, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MistBackground)
            .pointerInput(showDrawer) {
                var totalDrag = 0f

                detectVerticalDragGestures(
                    onVerticalDrag = { _, amount ->
                        totalDrag += amount
                    },
                    onDragEnd = {
                        if (totalDrag < -80f) showDrawer = true
                        if (totalDrag > 80f) showDrawer = false
                        totalDrag = 0f
                    }
                )
            }
    ) {
        MistForestBackground()

        if (showDrawer) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp)
            ) {
                Spacer(Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "APPLICATIONS",
                            color = MistSilver,
                            fontSize = 12.sp,
                            letterSpacing = 3.sp
                        )
                        Text(
                            "Your collection.",
                            color = MistText,
                            fontSize = 27.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Text(
                        "CLOSE",
                        color = MistMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable {
                            showDrawer = false
                        }.padding(10.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Surface(
                    color = MistPanel,
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BasicTextField(
                        value = search,
                        onValueChange = { search = it },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = MistText,
                            fontSize = 15.sp
                        ),
                        decorationBox = { innerTextField ->
                            Box(Modifier.padding(17.dp)) {
                                if (search.isEmpty()) {
                                    Text(
                                        "Search your applications...",
                                        color = MistMuted,
                                        fontSize = 14.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                Spacer(Modifier.height(20.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(
                        filteredApps,
                        key = { it.activityInfo.packageName }
                    ) { app ->
                        val name = app.loadLabel(
                            context.packageManager
                        ).toString()

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onOpenApp(app.activityInfo.packageName)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(19.dp))
                                    .background(MistPanel),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    name.take(1).uppercase(),
                                    color = MistSilver,
                                    fontSize = 24.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            Text(
                                name,
                                color = MistText,
                                fontSize = 12.sp,
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
                Spacer(Modifier.height(27.dp))

                Text(
                    "M I S T",
                    color = MistSilver,
                    fontSize = 17.sp,
                    letterSpacing = 5.sp,
                    fontWeight = FontWeight.Light
                )

                Text(
                    "S I L E N T   F O R E S T",
                    color = MistMuted,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(75.dp))

                Text(
                    clock,
                    color = MistText,
                    fontSize = 76.sp,
                    fontWeight = FontWeight.Light,
                    fontFamily = FontFamily.Serif
                )

                Text(
                    date.uppercase(),
                    color = MistSilver,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(38.dp))

                Surface(
                    color = MistPanel,
                    shape = RoundedCornerShape(22.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "THE SILENT FOREST",
                            color = MistSilver,
                            fontSize = 10.sp,
                            letterSpacing = 2.sp
                        )

                        Spacer(Modifier.height(9.dp))

                        Text(
                            "A quieter way to exist.",
                            color = MistText,
                            fontSize = 21.sp,
                            fontFamily = FontFamily.Serif
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "Find your space. Leave the noise behind.",
                            color = MistMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    "YOUR APPLICATIONS",
                    color = MistMuted,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp
                )

                Spacer(Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    apps.take(4).forEach { app ->
                        val name = app.loadLabel(
                            context.packageManager
                        ).toString()

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onOpenApp(app.activityInfo.packageName)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(55.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(MistPanel),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    name.take(1).uppercase(),
                                    color = MistSilver,
                                    fontSize = 23.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }

                            Spacer(Modifier.height(7.dp))

                            Text(
                                name,
                                color = MistText,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Surface(
                    color = Color(0xDD263748),
                    shape = RoundedCornerShape(25.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            search = ""
                            showDrawer = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(17.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "⌕    EXPLORE ALL APPLICATIONS",
                            color = MistText,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "SWIPE UP TO EXPLORE",
                    color = MistMuted,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
fun MistForestBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x303D607B),
                    Color.Transparent
                ),
                center = Offset(w * 0.55f, h * 0.48f),
                radius = w * 0.8f
            ),
            radius = w * 0.8f,
            center = Offset(w * 0.55f, h * 0.48f)
        )

        val farHill = Path().apply {
            moveTo(0f, h * 0.67f)
            cubicTo(w * 0.3f, h * 0.54f, w * 0.6f, h * 0.72f, w, h * 0.57f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(farHill, Color(0xFF101F2B))

        val nearHill = Path().apply {
            moveTo(0f, h * 0.79f)
            cubicTo(w * 0.35f, h * 0.66f, w * 0.7f, h * 0.84f, w, h * 0.72f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(nearHill, Color(0xFF091722))

        drawCircle(
            color = Color(0x183D6278),
            radius = w * 0.35f,
            center = Offset(w * 0.5f, h * 0.78f)
        )
    }
}
