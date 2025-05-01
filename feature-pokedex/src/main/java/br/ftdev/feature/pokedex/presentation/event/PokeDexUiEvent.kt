package br.ftdev.feature.pokedex.presentation.event

sealed interface PokeDexUiEvent {
    data class ShowSnackbar(val message: String) : PokeDexUiEvent
}