package br.ftdev.core.ui.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import br.ftdev.core.ui.component.toImageRequest
import coil.Coil
import coil.ImageLoader
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

            val request = this@loadBitmapFromUrl.toImageRequest(context)

            when (val result = imageLoader.execute(request)) {
                is SuccessResult -> {
                    when (val drawable = result.drawable) {
                        is BitmapDrawable -> drawable.bitmap
                        else -> drawable.toBitmap()
                    }.extractMutedColor()
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

fun Bitmap.extractMutedColor(
    defaultColor: Color = Color.LightGray
) = runCatching {
    val palette = Palette.from(this@extractMutedColor).generate()
    val mutedColor = palette.getMutedColor(palette.getDominantColor(defaultColor.toArgb()))
    Color(mutedColor)
}.onFailure { exception ->
    println("Error generating Palette or extracting color: ${exception.message}")
}.getOrDefault(defaultColor)

fun getVerticalGradient(colors: List<Color>) =
    Brush.verticalGradient(
        colors = colors,
        endY = 800f
    )

fun getHorizontalGradient(colors: List<Color>) =
    Brush.horizontalGradient(
        colors = colors,
        endX = 950f
    )

