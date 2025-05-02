package br.ftdev.feature.pokedex.presentation.state

import br.ftdev.core.domain.model.Pokemon

sealed interface PokedexUiState {
    data class Loading(
        val isInitialLoading: Boolean = false
    ) : PokedexUiState

    data class Success(
        val pokemonList: List<Pokemon>,
        val canLoadMore: Boolean
    ) : PokedexUiState

    data class Error(val message: String) : PokedexUiState
}