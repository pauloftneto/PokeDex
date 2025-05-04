package br.ftdev.core.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import br.ftdev.core.ui.R
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ImageRequest.ToAsyncImage(
    contentDescription: String?,
    contentScale: ContentScale = ContentScale.Fit,
    modifier: Modifier,
    onLoadingState: (Boolean) -> Unit = {}
) {
    val imageState = remember { mutableStateOf(true) }

    return AsyncImage(
        model = this,
        contentScale = contentScale,
        placeholder = painterResource(R.drawable.poke_placeholder),
        error = painterResource(R.drawable.poke_error),
        contentDescription = contentDescription,
        modifier = modifier,
        onSuccess = { imageState.value = false; onLoadingState(false) },
        onError = { imageState.value = false; onLoadingState(false) }
    )
}

@Composable
fun PokemonLogo(modifier: Modifier = Modifier) = Image(
    painter = painterResource(id = R.drawable.pokemon_logo),
    contentDescription = "Pokémon Logo",
    modifier = modifier
)
