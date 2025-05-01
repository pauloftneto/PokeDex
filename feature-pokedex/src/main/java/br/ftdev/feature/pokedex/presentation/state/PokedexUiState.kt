package br.ftdev.feature.pokedex.presentation.state

import br.ftdev.core.domain.model.Pokemon

sealed interface PokedexUiState {
    data object Loading : PokedexUiState
    data class Success(
        val pokemonList: List<Pokemon>,
        val canLoadMore: Boolean
    ) : PokedexUiState
    data class Error(val message: String) : PokedexUiState
}