package br.ftdev.core.ui.component

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import br.ftdev.core.ui.theme.PokemonTypeColor
import java.util.Locale

@Composable
fun String.TypeChip(modifier: Modifier = Modifier) {
    val typeColor = PokemonTypeColor.getTypeColor(this)

    AssistChip(
        modifier = modifier,
        onClick = { },
        label = {
            Text(this.replaceFirstChar {
                if (it.isLowerCase())
                    it.titlecase(Locale.getDefault())
                else
                    it.toString()
            })
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = typeColor,
            labelColor = Color.White
        ),
        border = null
    )
}