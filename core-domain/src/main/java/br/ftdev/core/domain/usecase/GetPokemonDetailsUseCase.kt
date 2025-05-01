package br.ftdev.core.domain.usecase

import br.ftdev.core.data.repository.PokemonRepository
import br.ftdev.core.domain.mapper.toDomain
import br.ftdev.core.domain.model.PokemonDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPokemonDetailsUseCase(
    private val pokemonRepository: PokemonRepository
) {
    suspend operator fun invoke(nameOrId: String): Flow<Result<PokemonDetails>> = flow {
        pokemonRepository.getPokemonDetails(nameOrId)
            .onSuccess { detailsDto ->
                emit(Result.success(detailsDto.toDomain()))
            }
            .onFailure { exception ->
                emit(Result.failure(exception))
            }
    }
}