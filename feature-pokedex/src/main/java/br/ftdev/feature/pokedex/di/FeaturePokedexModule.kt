package br.ftdev.feature.pokedex.di

import br.ftdev.feature.pokedex.presentation.PokeDexViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featurePokeDexModule = module {
    viewModel { PokeDexViewModel(get()) }
}