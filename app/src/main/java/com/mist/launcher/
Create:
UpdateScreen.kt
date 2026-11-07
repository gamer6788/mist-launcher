
package com.mist.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val MistBackground = Color(0xFF080D16)
private val MistSurface = Color(0xFF151E2B)
private val MistSilver = Color(0xFFDCE8F2)
private val MistBlue = Color(0xFF9BBBD4)
private val MistMuted = Color(0xFF8997A8)

@Composable
fun MistUpdateScreen(
    currentVersion: String,
    status: String,
    update: MistUpdate?,
    downloadProgress: Int,
    isDownloading: Boolean,
    isVerified: Boolean,
    onCheckUpdates: () -> Unit,
    onDownload: () -> Unit,
    onInstall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MistBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {

        Text(
            text = "MIST / SYSTEM",
            color = MistBlue,
            fontSize = 12.sp,
            letterSpacing = 4.sp
        )

        Text(
            text = "Software\nUpdates",
            color = MistSilver,
            fontSize = 36.sp,
            lineHeight = 42.sp,
            fontWeight = FontWeight.Light
        )

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MistSurface
            ),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "CURRENT VERSION",
                    color = MistMuted,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "MIST $currentVersion",
                    color = MistSilver,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Medium
                )

                HorizontalDivider(color = Color(0xFF293545))

                Text(
                    text = status,
                    color = MistBlue,
                    fontSize = 14.sp
                )

                Button(
                    onClick = onCheckUpdates,
                    enabled = !isDownloading,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MistBlue,
                        contentColor = MistBackground
                    )
                ) {
                    Text("CHECK FOR UPDATES")
                }
            }
        }

        if (update != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MistSurface
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "UPDATE AVAILABLE",
                        color = MistBlue,
                        fontSize = 11.sp,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "MIST ${update.versionName}",
                        color = MistSilver,
                        fontSize = 24.sp
                    )

                    Text(
                        text = update.releaseNotes,
                        color = MistMuted,
                        fontSize = 14.sp
                    )

                    if (isDownloading) {
                        LinearProgressIndicator(
                            progress = {
                                downloadProgress / 100f
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = MistBlue,
                            trackColor = Color(0xFF293545)
                        )

                        Text(
                            text = "$downloadProgress% downloaded",
                            color = MistSilver,
                            fontSize = 13.sp
                        )
                    }

                    if (isVerified) {
                        Text(
                            text = "✓ APK VERIFIED",
                            color = MistBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = onInstall,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("INSTALL UPDATE")
                        }
                    } else {
                        Button(
                            onClick = onDownload,
                            enabled = !isDownloading,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MistBlue,
                                contentColor = MistBackground
                            )
                        ) {
                            Text(
                                if (isDownloading) {
                                    "DOWNLOADING..."
                                } else {
                                    "DOWNLOAD UPDATE"
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "MIST FUSION  •  BUILT FOR SILENCE",
            color = MistMuted,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
