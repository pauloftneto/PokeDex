package br.ftdev.feature.pokedex.details.di

import br.ftdev.feature.pokedex.details.presentation.PokemonDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val pokemonDetailsModule = module {
    viewModel { params ->
        PokemonDetailsViewModel(get(), params.get())
    }
}