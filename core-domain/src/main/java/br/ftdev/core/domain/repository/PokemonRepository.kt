package br.ftdev.core.domain.repository

import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.model.PokemonDetails

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>>
    suspend fun getPokemonDetails(nameOrId: String): Result<PokemonDetails>
    suspend fun refreshPokemonList(): Result<Unit>
}