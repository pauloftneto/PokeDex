package br.ftdev.core.data.repository

import br.ftdev.core.data.remote.api.PokeApiService
import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService
) : PokemonRepository {
    override suspend fun getPokemonList(limit: Int, offset: Int): Result<PokemonListResponse> {
        return withContext(Dispatchers.IO) {
            runCatching {
                pokeApiService.getPokemonList(limit, offset)
            }
        }
    }

    override suspend fun getPokemonDetails(nameOrId: String): Result<PokemonDetailsResponse> {
        return withContext(Dispatchers.IO) {
            runCatching {
                pokeApiService.getPokemonDetails(nameOrId)
            }
        }
    }
}