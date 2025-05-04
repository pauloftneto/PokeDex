package br.ftdev.feature.pokedex.details.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ftdev.core.domain.usecase.GetPokemonDetailsUseCase
import br.ftdev.core.ui.component.error.getErrorMessage
import br.ftdev.feature.pokedex.details.presentation.state.PokemonDetailsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailsViewModel(
    private val getPokemonDetailsUseCase: GetPokemonDetailsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailsUiState>(PokemonDetailsUiState.Loading)
    val uiState: StateFlow<PokemonDetailsUiState> = _uiState.asStateFlow()


    private val pokemonId: Int = checkNotNull(savedStateHandle["pokemonId"]) {
        "Pokemon ID não encontrado na navegação!"
    }

    init {
        fetchDetails()
    }

    fun fetchDetails() {
        _uiState.value = PokemonDetailsUiState.Loading
        viewModelScope.launch {
            getPokemonDetailsUseCase(pokemonId.toString())
                .collect { result ->
                    result.onSuccess { pokemon ->
                        _uiState.value = PokemonDetailsUiState.Success(pokemon)
                    }.onFailure { exception ->
                        handlePokemonFetchFailure(exception)
                    }
                }
        }
    }

    private fun handlePokemonFetchFailure(exception: Throwable) {
        val errorMsg = exception.getErrorMessage()
        _uiState.value = PokemonDetailsUiState.Error(errorMsg)
    }

}