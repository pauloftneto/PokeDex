package br.ftdev.feature.pokedex.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ftdev.core.analytics.AnalyticsTracker
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.usecase.GetPokemonListUseCase
import br.ftdev.core.domain.usecase.RefreshPokemonListUseCase
import br.ftdev.core.ui.component.error.getErrorMessage
import br.ftdev.feature.pokedex.presentation.event.PokeDexUiEvent
import br.ftdev.feature.pokedex.presentation.state.PokedexUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

class PokeDexViewModel(
    private val getPokemonListUseCase: GetPokemonListUseCase,
    private val refreshPokemonListUseCase: RefreshPokemonListUseCase,
    private val analytics: AnalyticsTracker
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<PokedexUiState>(PokedexUiState.Loading(isInitialLoading = true))
    val uiState: StateFlow<PokedexUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _eventChannel = MutableSharedFlow<PokeDexUiEvent>(replay = 0)
    val eventFlow: SharedFlow<PokeDexUiEvent> = _eventChannel.asSharedFlow()

    private var currentPage = 0
    private val pokemonList = mutableListOf<Pokemon>()
    private var fetchJob: Job? = null
    private var canLoadMore = true


    init {
        analytics.trackScreen("PokedexScreen")
        fetchPokemonList()

        viewModelScope.launch {
            searchQuery.collectLatest { query ->
                filterAndUpdateUi(query)
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        analytics.trackEvent("search_query", mapOf("query" to query))
        _searchQuery.value = query
    }

    fun onPokemonClick(pokemonId: Int) {
        analytics.trackEvent("pokemon_selected", mapOf("pokemon_id" to pokemonId.toString()))
    }

    private fun filterAndUpdateUi(query: String) {
        val currentState = _uiState.value

        if (pokemonList.isNotEmpty()) {
            val filteredList = if (query.isBlank()) {
                pokemonList.toList()
            } else {
                pokemonList.filter { pokemon ->
                    pokemon.name.contains(query, ignoreCase = true) ||
                            pokemon.id.toString() == query.trim()
                }
            }
            _uiState.value = PokedexUiState.Success(
                pokemonList = filteredList,
                canLoadMore = canLoadMore
            )
        } else if (currentState is PokedexUiState.Success) {
            _uiState.value = PokedexUiState.Success(emptyList(), false)
        }
    }

    fun fetchPokemonList(forceRefresh: Boolean = false) {
        analytics.trackEvent("fetch_pokemon_list", mapOf("forceRefresh" to forceRefresh.toString()))
        if (forceRefresh && !_isRefreshing.value) {
            cancelFetchListJob()
        }

        if (forceRefresh) {
            resetState()
        }

        fetchJob = viewModelScope.launch {
            getPokemonListUseCase(limit = PAGE_SIZE, offset = currentPage * PAGE_SIZE)
                .collect { result ->
                    result.onSuccess { newPokemon ->
                        handlePokemonFetchSuccess(newPokemon)
                    }.onFailure { exception ->
                        handlePokemonFetchFailure(exception)
                    }
                }
        }
    }

    private fun handlePokemonFetchSuccess(newPokemon: List<Pokemon>) {
        canLoadMore = newPokemon.size == PAGE_SIZE
        analytics.trackEvent(
            "pokemon_list_loaded",
            mapOf(
                "page" to currentPage.toString(),
                "loadedCount" to newPokemon.size.toString()
            )
        )
        pokemonList.addAll(newPokemon)
        if (newPokemon.isNotEmpty()) {
            currentPage++
        }
        _uiState.value = PokedexUiState.Success(
            pokemonList = pokemonList.toList(),
            canLoadMore = canLoadMore
        )
    }

    private suspend fun handlePokemonFetchFailure(exception: Throwable) {
        val errorMsg = exception.getErrorMessage()
        analytics.trackEvent("pokemon_list_error", mapOf("error" to errorMsg))

        if (pokemonList.isEmpty()) {
            _uiState.value = PokedexUiState.Error(errorMsg)
            canLoadMore = false
        } else {
            _eventChannel.emit(PokeDexUiEvent.ShowSnackbar(errorMsg))
            _uiState.value = PokedexUiState.Success(
                pokemonList = pokemonList.toList(),
                canLoadMore = false
            )
        }
    }

    private fun cancelFetchListJob() {
        fetchJob?.cancel()
        fetchJob = null
    }

    override fun onCleared() {
        super.onCleared()
        cancelFetchListJob()
    }

    fun refreshList() {
        if (_isRefreshing.value) return

        _isRefreshing.value = true
        viewModelScope.launch {
            refreshPokemonListUseCase().first()
                .onSuccess {
                    analytics.trackEvent("list_refreshed")
                    fetchPokemonList(forceRefresh = true)
                }
                .onFailure { error ->
                    handleRefreshError(error)
                }.also {
                    _isRefreshing.value = false
                }
        }
    }

    private suspend fun handleRefreshError(error: Throwable) {
        val errorMsg = error.getErrorMessage(isRefresh = true)
        analytics.trackEvent("list_refresh_error", mapOf("error" to errorMsg))
        _eventChannel.emit(PokeDexUiEvent.ShowSnackbar(errorMsg))
    }

    private fun resetState() {
        currentPage = 0
        pokemonList.clear()
        canLoadMore = true
        _uiState.value = PokedexUiState.Loading(
            isInitialLoading = _isRefreshing.value.not(),
        )
    }
}