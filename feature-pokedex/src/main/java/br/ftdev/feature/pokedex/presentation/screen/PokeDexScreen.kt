package br.ftdev.feature.pokedex.presentation.screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.ui.R
import br.ftdev.core.ui.component.ErrorMessage
import br.ftdev.core.ui.component.LoadingIndicator
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.core.ui.util.getVerticalGradient
import br.ftdev.core.ui.util.loadBitmapFromUrl
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
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }

    CollectEvents(eventFlow = viewModel.eventFlow, snackbarHostState = snackbarHostState)

    PokemonAppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = dimensionResource(R.dimen.padding_medium))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pokemon_logo),
                    contentDescription = "Pokémon Logo",
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = dimensionResource(R.dimen.padding_large))
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = dimensionResource(R.dimen.padding_medium)),
                    placeholder = { Text("Buscar Pokémon") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Filtrar") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpar")
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        keyboardController?.hide()
                    })
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
                                            if (searchQuery.isBlank()) "Nenhum Pokémon encontrado."
                                            else "Nenhum Pokémon corresponde a \"$searchQuery\""
                                        )
                                    }
                                } else {
                                    PokemonGrid(
                                        pokemonList = state.pokemonList,
                                        canLoadMore = state.canLoadMore && searchQuery.isBlank(),
                                        onLoadMore = { viewModel.fetchPokemonList() },
                                        onPokemonClick = onPokemonClick,
                                    )
                                }
                            }

                            is PokedexUiState.Error -> ErrorMessage(
                                message = state.message,
                                onRetry = { viewModel.fetchPokemonList(forceRefresh = true) }
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

    val image = ImageRequest.Builder(LocalContext.current)
        .data(pokemon.imageUrl)
        .crossfade(true)
        .build()

    val context = LocalContext.current
    var cardBackgroundColor by remember { mutableStateOf(Color.LightGray) }

    LaunchedEffect(pokemon.imageUrl) {
        val extractedColor = pokemon.imageUrl.loadBitmapFromUrl(context)
        cardBackgroundColor = extractedColor
    }

    val gradientBrush = getVerticalGradient(
        listOf(
            cardBackgroundColor.copy(alpha = 0.2f),
            cardBackgroundColor.copy(alpha = 0.8f),
            cardBackgroundColor
        )
    )

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .size(dimensionResource(R.dimen.card_size))
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.card_corner_radius_large))),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = gradientBrush)
                .padding(dimensionResource(id = R.dimen.padding_small))
        ) {
            AsyncImage(
                model = image,
                contentDescription = pokemon.name,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
                    .offset(y = (-10).dp)
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = dimensionResource(id = R.dimen.padding_medium)),
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.chip_corner_radius))
            ) {
                Text(
                    text = "#${pokemon.id.toString().padStart(3, '0')} ${pokemon.name}",
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