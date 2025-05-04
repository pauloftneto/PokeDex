package br.ftdev.core.ui.component

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.ftdev.core.ui.theme.AppShapes
import br.ftdev.core.ui.theme.PokeDarkRed
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.shimmerPlaceholder(
    visible: Boolean
): Modifier = this
    .placeholder(
        visible = visible,
        highlight = PlaceholderHighlight.shimmer(PokeDarkRed),
        color = Color.LightGray,
        shape = AppShapes.medium
    )

