package br.ftdev.feature.pokedex.presentation.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.ui.R
import br.ftdev.core.ui.component.LoadingIndicator
import br.ftdev.core.ui.component.PokemonLogo
import br.ftdev.core.ui.component.SearchBar
import br.ftdev.core.ui.component.ToAsyncImage
import br.ftdev.core.ui.component.error.ErrorMessage
import br.ftdev.core.ui.component.eventSnackbarHost
import br.ftdev.core.ui.component.shimmerPlaceholder
import br.ftdev.core.ui.component.toImageRequest
import br.ftdev.core.ui.component.toPaddedId
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.core.ui.util.getHorizontalGradient
import br.ftdev.core.ui.util.loadBitmapFromUrl
import br.ftdev.feature.pokedex.presentation.PokeDexViewModel
import br.ftdev.feature.pokedex.presentation.event.PokeDexUiEvent
import br.ftdev.feature.pokedex.presentation.state.PokedexUiState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokedexScreen(
    viewModel: PokeDexViewModel = koinViewModel(),
    onPokemonClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    var isGrid by remember { mutableStateOf(false) }

    CollectEvents(eventFlow = viewModel.eventFlow, snackbarHostState = snackbarHostState)

    PokemonAppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { isGrid = !isGrid },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = if (isGrid) Icons.AutoMirrored.Filled.List else
                            ImageVector.vectorResource(R.drawable.grid_view_24px),
                        contentDescription = if (isGrid) "Mostrar como lista" else "Mostrar como grid"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            ) {
                PokemonLogo(
                    Modifier
                        .fillMaxWidth(0.6f)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = dimensionResource(R.dimen.padding_large))
                )
                searchQuery.SearchBar(
                    placeholder = stringResource(R.string.search_placeholder),
                    onSearchQueryChanged = viewModel::onSearchQueryChanged
                )

                PullToRefreshBox(
                    modifier = Modifier
                        .fillMaxWidth(),
                    isRefreshing = isRefreshing,
                    onRefresh = viewModel::refreshList
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        when (val state = uiState) {
                            is PokedexUiState.Loading -> {
                                LoadingIndicator(isInitialLoading = state.isInitialLoading)
                            }

                            is PokedexUiState.Success -> {
                                if (state.pokemonList.isEmpty()) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (searchQuery.isBlank()) stringResource(R.string.empty_list)
                                            else stringResource(
                                                R.string.empty_list_with_query,
                                                searchQuery
                                            )
                                        )
                                    }
                                } else {
                                    PokemonGrid(
                                        isGrid = isGrid,
                                        pokemonList = state.pokemonList,
                                        canLoadMore = state.canLoadMore && searchQuery.isBlank(),
                                        onLoadMore = { viewModel.fetchPokemonList() },
                                        onPokemonClick = onPokemonClick,
                                    )
                                }
                            }

                            is PokedexUiState.Error -> state.message.ErrorMessage(
                                onRetry = { viewModel.fetchPokemonList(forceRefresh = true) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PokemonGrid(
    isGrid: Boolean,
    pokemonList: List<Pokemon>,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit,
    onPokemonClick: (Int) -> Unit
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = if (isGrid)
            GridCells.Adaptive(minSize = dimensionResource(R.dimen.min_size))
        else GridCells.Fixed(
            1
        ),
        state = listState,
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
    ) {
        items(pokemonList, key = { it.id }) { pokemon ->
            if (isGrid) {
                PokemonCard(
                    pokemon = pokemon,
                    onClick = { onPokemonClick(pokemon.id) }
                )
            } else {
                PokemonList(
                    pokemon = pokemon,
                    onClick = { onPokemonClick(pokemon.id) }
                )
            }
        }

        if (canLoadMore) {
            item {
                listState.onEndReached(onLoadMore = onLoadMore)

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
fun LazyGridState.onEndReached(
    onLoadMore: () -> Unit,
    loadMoreThreshold: Int = 5,
    enabled: Boolean = true
) {
    LaunchedEffect(this, enabled) {
        if (!enabled) return@LaunchedEffect
        snapshotFlow { layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .filterNotNull()
            .filter { last -> last >= layoutInfo.totalItemsCount - loadMoreThreshold }
            .distinctUntilChanged()
            .collect { onLoadMore() }
    }
}

@Composable
fun PokemonCard(
    pokemon: Pokemon,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val image = pokemon.imageUrl?.toImageRequest(LocalContext.current)
    val gradientBrush = pokemon.imageUrl.gradientBrush()
    val imageState = remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .size(dimensionResource(R.dimen.card_size))
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_medium)))
            .shimmerPlaceholder(imageState.value),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            image?.ToAsyncImage(
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            ) {
                imageState.value = it
            }
            PokemonNameChip(
                pokemon = pokemon,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun PokemonNameChip(
    pokemon: Pokemon,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(bottom = dimensionResource(id = R.dimen.padding_medium)),
        color = Color.Black.copy(alpha = 0.6f),
        shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_corner_radius))
    ) {
        Text(
            text = "#${pokemon.id.toPaddedId()} ${pokemon.name}",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = dimensionResource(id = R.dimen.padding_medium),
                vertical = dimensionResource(id = R.dimen.padding_extra_small)
            )
        )
    }
}

@Composable
fun PokemonList(
    pokemon: Pokemon,
    onClick: () -> Unit,
) {

    val bgGradient = pokemon.imageUrl.gradientBrush()
    val imageState = remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_medium)))
            .shimmerPlaceholder(imageState.value),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(bgGradient)
                .padding(horizontal = 8.dp)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PokemonInfo(pokemon)
                PokemonImage(pokemon, imageState)

            }
        }
    }
}


@Composable
fun PokemonInfo(pokemon: Pokemon) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "#${pokemon.id.toPaddedId()}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_small))
        )
        Text(
            text = pokemon.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PokemonImage(
    pokemon: Pokemon,
    imageState: MutableState<Boolean>
) {
    val image = pokemon.imageUrl?.toImageRequest(LocalContext.current)
    Box(
        modifier = Modifier
            .size(
                width = 130.dp,
                height = 90.dp
            )
            .offset(x = 20.dp)
            .background(
                Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(
                    topStart = dimensionResource(R.dimen.corner_radius_xXXLarge),
                    bottomStart = dimensionResource(R.dimen.corner_radius_xXXLarge)
                )
            )
    ) {
        image?.ToAsyncImage(
            contentDescription = pokemon.name,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.Center)
        ) {
            imageState.value = it
        }
    }
}


@Composable
fun String?.gradientBrush(
    defaultColor: Color = Color.LightGray
): Brush {
    val context = LocalContext.current
    val dominantColor by produceState(defaultColor, this@gradientBrush) {
        value = this@gradientBrush.loadBitmapFromUrl(context)
    }
    return getHorizontalGradient(
        listOf(
            dominantColor.copy(alpha = 0.4f),
            dominantColor.copy(alpha = 0.5f),
            dominantColor.copy(alpha = 0.7f),
            dominantColor
        )
    )
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
                    snackbarHostState.eventSnackbarHost(
                        event.message
                    )
                }
            }
        }
    }
}
