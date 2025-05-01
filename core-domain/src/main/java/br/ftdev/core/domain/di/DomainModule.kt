package br.ftdev.core.domain.di

import br.ftdev.core.domain.usecase.GetPokemonDetailsUseCase
import br.ftdev.core.domain.usecase.GetPokemonListUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetPokemonListUseCase(get()) }
    factory { GetPokemonDetailsUseCase(get()) }
}