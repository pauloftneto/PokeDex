package br.ftdev.core.domain.usecase

import br.ftdev.core.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RefreshPokemonListUseCase(
    private val pokemonRepository: PokemonRepository
) {
    operator fun invoke(): Flow<Result<Unit>> = flow {
        pokemonRepository.refreshPokemonList().onSuccess { result ->
            emit(Result.success(result))
        }.onFailure { exception ->
            emit(Result.failure(exception))
        }
    }
}