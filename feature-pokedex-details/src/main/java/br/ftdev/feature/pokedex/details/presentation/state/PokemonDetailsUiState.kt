package br.ftdev.feature.pokedex.details.presentation.state

import br.ftdev.core.domain.model.PokemonDetails

sealed interface PokemonDetailsUiState {
    data object Loading : PokemonDetailsUiState
    data class Success(val pokemon: PokemonDetails) : PokemonDetailsUiState
    data class Error(val message: String) : PokemonDetailsUiState
}