package br.ftdev.core.domain.usecase

import br.ftdev.core.data.repository.PokemonRepository
import br.ftdev.core.domain.mapper.toDomain
import br.ftdev.core.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPokemonListUseCase(
    private val pokemonRepository: PokemonRepository
) {
    suspend operator fun invoke(limit: Int, offset: Int): Flow<Result<List<Pokemon>>> = flow {
        pokemonRepository.getPokemonList(limit, offset)
            .onSuccess { responseDto ->
                val domainList = responseDto.results.mapNotNull { it.toDomain() }
                emit(Result.success(domainList))
            }
            .onFailure { exception ->
                emit(Result.failure(exception))
            }
    }
}