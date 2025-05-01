package br.ftdev.feature.pokedex.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.ui.R
import br.ftdev.core.ui.component.ErrorMessage
import br.ftdev.core.ui.component.LoadingIndicator
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.feature.pokedex.presentation.PokeDexViewModel
import br.ftdev.feature.pokedex.presentation.event.PokeDexUiEvent
import br.ftdev.feature.pokedex.presentation.state.PokedexUiState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    viewModel: PokeDexViewModel = koinViewModel(),
    onPokemonClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(eventFlow = viewModel.eventFlow, snackbarHostState = snackbarHostState)

    PokemonAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Pokédex") })
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is PokedexUiState.Loading -> LoadingIndicator()
                    is PokedexUiState.Success -> PokemonGrid(
                        pokemonList = state.pokemonList,
                        canLoadMore = state.canLoadMore,
                        onLoadMore = { viewModel.fetchPokemonList() },
                        onPokemonClick = onPokemonClick
                    )

                    is PokedexUiState.Error -> ErrorMessage(
                        message = state.message,
                        onRetry = { viewModel.fetchPokemonList(forceRefresh = true) }
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonGrid(
    pokemonList: List<Pokemon>,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    onPokemonClick: (Int) -> Unit
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = dimensionResource(R.dimen.min_size)),
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        items(pokemonList, key = { it.id }) { pokemon ->
            PokemonCard(
                pokemon = pokemon,
                onClick = { onPokemonClick(pokemon.id) }
            )
        }

        if (canLoadMore) {
            item {
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: -1
                            val totalItems = listState.layoutInfo.totalItemsCount

                            if (lastVisibleItemIndex >= totalItems - 5 && totalItems > 0) {
                                onLoadMore()
                            }
                        }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_large)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}


@Composable
fun PokemonCard(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.padding_small))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemon.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = pokemon.name,
                modifier = Modifier.size(dimensionResource(R.dimen.image_size))
            )
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
            Text(
                text = "#${pokemon.id} ${pokemon.name}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
fun CollectEvents(
    eventFlow: SharedFlow<PokeDexUiEvent>,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(key1 = eventFlow) {
        eventFlow.collectLatest { event ->
            when (event) {
                is PokeDexUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }
}