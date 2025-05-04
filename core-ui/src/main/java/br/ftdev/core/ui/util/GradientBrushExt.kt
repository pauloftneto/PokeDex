package br.ftdev.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

@Composable
fun String?.rememberPokemonGradientBrush(
    defaultColor: Color = Color.LightGray
): Brush {
    val context = LocalContext.current
    val dominantColor by produceState(defaultColor, this@rememberPokemonGradientBrush) {
        value = this@rememberPokemonGradientBrush.loadBitmapFromUrl(context)
    }
    return getVerticalGradient(
        listOf(
            dominantColor.copy(alpha = 0.2f),
            dominantColor.copy(alpha = 0.8f),
            dominantColor
        )
    )
}