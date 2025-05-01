package br.ftdev.core.data.remote.api

import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PokeApiService {
    companion object {
        const val BASE_URL = "https://pokeapi.co/api/v2/"
    }
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0
    ): PokemonListResponse

    @GET("pokemon/{nameOrId}")
    suspend fun getPokemonDetails(
        @Path("nameOrId") nameOrId: String
    ): PokemonDetailsResponse
}