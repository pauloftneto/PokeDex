package br.ftdev.core.ui.component

import androidx.compose.ui.Modifier
import br.ftdev.core.ui.theme.AppShapes
import br.ftdev.core.ui.theme.LightGray
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
        color = LightGray,
        shape = AppShapes.medium
    )

