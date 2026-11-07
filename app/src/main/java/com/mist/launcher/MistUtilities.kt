
package com.mist.launcher

import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.drawable.Drawable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun currentTime(): String {
    return SimpleDateFormat(
        "HH:mm",
        Locale.getDefault()
    ).format(Date())
}

fun currentDate(): String {
    return SimpleDateFormat(
        "EEEE, dd MMMM",
        Locale.getDefault()
    ).format(Date())
}

fun drawableBitmap(drawable: Drawable): Bitmap {
    val width = drawable.intrinsicWidth
        .takeIf { it > 0 } ?: 96

    val height = drawable.intrinsicHeight
        .takeIf { it > 0 } ?: 96

    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = AndroidCanvas(bitmap)

    drawable.setBounds(
        0,
        0,
        canvas.width,
        canvas.height
    )

    drawable.draw(canvas)

    return bitmap
}
