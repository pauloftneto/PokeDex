package br.ftdev.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun String?.loadBitmapFromUrl(
    context: Context,
    defaultColor: Color = Color.LightGray
): Color =
    withContext(Dispatchers.IO) {
        if (this@loadBitmapFromUrl.isNullOrBlank()) {
            println("Error loading image from URL: $this - Image URL is null or blank")
            return@withContext defaultColor
        }

        runCatching {
            val imageLoader: ImageLoader = Coil.imageLoader(context)

            val request: ImageRequest = ImageRequest.Builder(context)
                .data(this@loadBitmapFromUrl)
                .allowHardware(false)
                .build()

            when (val result = imageLoader.execute(request)) {
                is SuccessResult -> {
                    when (val drawable = result.drawable) {
                        is BitmapDrawable -> drawable.bitmap
                        else -> drawable.toBitmap()
                    }.extractVibrantColor()
                }

                else -> {
                    println("Coil failed to load image from URL: $this - Result: $result")
                    defaultColor
                }
            }
        }.onFailure { exception ->
            println("Exception during image loading from URL $this@loadBitmapFromUrl: ${exception.message}")
        }.getOrDefault(defaultColor)
    }

fun Bitmap.extractVibrantColor(
    defaultColor: Color = Color.LightGray
) = runCatching {
    val palette = Palette.from(this@extractVibrantColor).generate()
    val vibrantColor = palette.getVibrantColor(palette.getDominantColor(defaultColor.toArgb()))
    Color(vibrantColor)
}.onFailure { exception ->
    println("Error generating Palette or extracting color: ${exception.message}")
}.getOrDefault(defaultColor)

fun getVerticalGradient(colors: List<Color>) =
    Brush.verticalGradient(
        colors = colors,
        startY = 0f,
        endY = 800f
    )

