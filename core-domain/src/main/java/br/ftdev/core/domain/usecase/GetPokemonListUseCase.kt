package br.ftdev.core.domain.usecase

import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPokemonListUseCase(
    private val pokemonRepository: PokemonRepository
) {
    operator fun invoke(limit: Int, offset: Int): Flow<Result<List<Pokemon>>> = flow {
        pokemonRepository.getPokemonList(limit, offset)
            .onSuccess { result ->
                emit(Result.success(result))
            }
            .onFailure { exception ->
                emit(Result.failure(exception))
            }
    }
}