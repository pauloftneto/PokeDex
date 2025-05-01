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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import br.ftdev.core.domain.model.Pokemon
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
    viewModel: PokeDexViewModel = koinViewModel(), // Injeta ViewModel via Koin
    // Adicione callbacks de navegação se necessário
    // onPokemonClick: (Int) -> Unit
) {
    // Observa o estado principal da UI
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado para o Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // Coleta os eventos one-time do ViewModel
    CollectEvents(eventFlow = viewModel.eventFlow, snackbarHostState = snackbarHostState)

    PokemonAppTheme { // Aplica o tema do core_ui
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Pokédex") }) // Exemplo de TopAppBar
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) } // Host para Snackbar
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Aplica padding do Scaffold
            ) {
                when (val state = uiState) {
                    is PokedexUiState.Loading -> LoadingIndicator()
                    is PokedexUiState.Success -> PokemonGrid(
                        pokemonList = state.pokemonList,
                        canLoadMore = state.canLoadMore,
                        onLoadMore = { viewModel.fetchPokemonList() }
                        // onPokemonClick = onPokemonClick // Passa callback de clique
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
    modifier: Modifier = Modifier
    // onPokemonClick: (Int) -> Unit
) {
    val listState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp), // Grid adaptável
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(pokemonList, key = { it.id }) { pokemon ->
            PokemonCard(
                pokemon = pokemon
                // onClick = { onPokemonClick(pokemon.id) }
            )
        }

        // Item no final para carregar mais ou indicar fim
        if (canLoadMore) {
            item {
                // Trigger para carregar mais quando este item estiver próximo de ser visível
                LaunchedEffect(listState) {
                    snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                        .collect { visibleItems ->
                            val lastVisibleItemIndex = visibleItems.lastOrNull()?.index ?: -1
                            val totalItems = listState.layoutInfo.totalItemsCount
                            // Carrega mais quando faltarem poucos itens para o fim
                            if (lastVisibleItemIndex >= totalItems - 5 && totalItems > 0) {
                                onLoadMore()
                            }
                        }
                }
                // Indicador visual de que está carregando mais
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
        // Opcional: mostrar algo quando não houver mais itens (canLoadMore == false)
        // item { if (!canLoadMore) Text("Fim da lista") }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonCard(
    pokemon: Pokemon,
    modifier: Modifier = Modifier
    // onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        // onClick = onClick, // Torna o card clicável
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pokemon.imageUrl)
                    .crossfade(true) // Efeito de fade na imagem
                    // .placeholder(R.drawable.placeholder) // Adicione um placeholder se quiser
                    // .error(R.drawable.error_image) // Adicione uma imagem de erro se quiser
                    .build(),
                contentDescription = pokemon.name,
                modifier = Modifier.size(96.dp) // Tamanho da imagem
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "#${pokemon.id} ${pokemon.name}",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

// Composable para coletar eventos e mostrar Snackbar
@Composable
fun CollectEvents(
    eventFlow: SharedFlow<PokeDexUiEvent>,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(key1 = eventFlow) { // Recoleta se o eventFlow mudar (improvável aqui)
        eventFlow.collectLatest { event -> // collectLatest cancela ações anteriores se um novo evento chegar rápido
            when (event) {
                is PokeDexUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
                // Handle other events here
            }
        }
    }
}