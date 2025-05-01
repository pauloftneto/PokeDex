package br.ftdev.feature.pokedex.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.usecase.GetPokemonListUseCase
import br.ftdev.feature.pokedex.presentation.event.PokeDexUiEvent
import br.ftdev.feature.pokedex.presentation.state.PokedexUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokeDexViewModel(
    private val getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokedexUiState>(PokedexUiState.Loading)
    val uiState: StateFlow<PokedexUiState> = _uiState.asStateFlow()

    private val _eventChannel = MutableSharedFlow<PokeDexUiEvent>(replay = 0)
    val eventFlow: SharedFlow<PokeDexUiEvent> = _eventChannel.asSharedFlow()

    private var currentPage = 0
    private val pokemonList = mutableListOf<Pokemon>()
    private var fetchJob: Job? = null
    private var canLoadMore = true

    companion object {
        private const val PAGE_SIZE = 20
    }

    init {
        fetchPokemonList()
    }

    fun fetchPokemonList(forceRefresh: Boolean = false) {
        if (fetchJob?.isActive == true || (!canLoadMore && !forceRefresh)) {
            return
        }

        if (forceRefresh) {
            currentPage = 0
            pokemonList.clear()
            canLoadMore = true
            _uiState.value = PokedexUiState.Loading
        }

        fetchJob = viewModelScope.launch {

            getPokemonListUseCase(limit = PAGE_SIZE, offset = currentPage * PAGE_SIZE)
                .collect { result ->
                    result.onSuccess { newPokemon ->
                        canLoadMore = newPokemon.size == PAGE_SIZE
                        pokemonList.addAll(newPokemon)
                        currentPage++
                        _uiState.value = PokedexUiState.Success(
                            pokemonList = pokemonList.toList(),
                            canLoadMore = canLoadMore
                        )
                    }.onFailure { exception ->

                        val errorMsg = exception.message ?: "Erro desconhecido ao buscar Pokémon"
                        if (pokemonList.isEmpty()) {
                            _uiState.value = PokedexUiState.Error(errorMsg)
                            canLoadMore = false
                        }else {
                            _eventChannel.emit(PokeDexUiEvent.ShowSnackbar(errorMsg))
                            _uiState.value = PokedexUiState.Success(
                                pokemonList = pokemonList.toList(),
                                canLoadMore = canLoadMore
                            )
                        }
                        println("Erro ao carregar Pokémon: $exception")
                    }
                }
        }
    }
}