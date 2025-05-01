package br.ftdev.core.domain.usecase

import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPokemonDetailsUseCase(
    private val pokemonRepository: PokemonRepository
) {
    suspend operator fun invoke(nameOrId: String): Flow<Result<PokemonDetails>> = flow {
        pokemonRepository.getPokemonDetails(nameOrId)
            .onSuccess { result ->
                emit(Result.success(result))
            }
            .onFailure { exception ->
                emit(Result.failure(exception))
            }
    }
}