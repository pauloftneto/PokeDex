package br.ftdev.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.ftdev.core.ui.theme.PokemonAppTheme

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier, isInitialLoading: Boolean = false) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (isInitialLoading) {
            CircularProgressIndicator()
        } else {
            LinearProgressIndicator(
                modifier = modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}