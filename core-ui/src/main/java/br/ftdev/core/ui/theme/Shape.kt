package br.ftdev.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import br.ftdev.core.ui.R

val AppShapes = Shapes(
    small = RoundedCornerShape(R.dimen.padding_small),
    medium = RoundedCornerShape(R.dimen.padding_medium),
    large = RoundedCornerShape(R.dimen.padding_large)
)