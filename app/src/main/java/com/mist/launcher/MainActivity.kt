package com.mist.launcher

import android.content.Intent
import android.os.Bundle
import android.content.pm.ResolveInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MistBackground = Color(0xFF0A111B)
private val MistPanel = Color(0xFF1B2938)
private val MistText = Color(0xFFE6EDF5)
private val MistMuted = Color(0xFFA9B9CA)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MistHome(
                onOpenApp = { packageName ->
                    val launchIntent =
                        packageManager.getLaunchIntentForPackage(packageName)

                    launchIntent?.let { startActivity(it) }
                },
                apps = remember {
                    val intent = Intent(Intent.ACTION_MAIN).apply {
                        addCategory(Intent.CATEGORY_LAUNCHER)
                    }

                    packageManager.queryIntentActivities(intent, 0)
                        .filter {
                            it.activityInfo.packageName != packageName
                        }
                        .distinctBy {
                            it.activityInfo.packageName
                        }
                        .sortedBy {
                            it.loadLabel(packageManager).toString()
                                .lowercase()
                        }
                }
            )
        }
    }
}

@Composable
fun MistHome(
    apps: List<ResolveInfo>,
    onOpenApp: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }

    val filteredApps = apps.filter {
        it.loadLabel(
            androidx.compose.ui.platform.LocalContext.current.packageManager
        ).toString().contains(search, ignoreCase = true)
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MistBackground)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 22.dp)
        ) {
            Spacer(Modifier.height(30.dp))

            Text(
                text = "M I S T",
                color = MistText,
                fontSize = 19.sp
            )

            Text(
                text = "A quieter way to exist.",
                color = MistMuted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(45.dp))

            Text(
                text = "Your space.",
                color = MistText,
                fontSize = 32.sp
            )

            Text(
                text = "Applications",
                color = MistMuted,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = {
                    Text("Find an application")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(Modifier.height(18.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredApps) { app ->
                    val name = app.loadLabel(
                        androidx.compose.ui.platform.LocalContext.current.packageManager
                    ).toString()

                    Surface(
                        color = MistPanel,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onOpenApp(app.activityInfo.packageName)
                            }
                    ) {
                        Text(
                            text = name,
                            color = MistText,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                }
            }
        }
    }
}