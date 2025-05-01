package br.ftdev.core.data.repository

import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListResponse

internal interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): Result<PokemonListResponse>
    suspend fun getPokemonDetails(nameOrId: String): Result<PokemonDetailsResponse>
}