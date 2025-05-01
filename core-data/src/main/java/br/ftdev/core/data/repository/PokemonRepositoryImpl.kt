package br.ftdev.core.data.repository

import br.ftdev.core.data.local.dao.PokemonDao
import br.ftdev.core.data.local.dao.PokemonDetailsDao
import br.ftdev.core.data.mapper.toDomain
import br.ftdev.core.data.mapper.toEntity
import br.ftdev.core.data.remote.api.PokeApiService
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.repository.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class PokemonRepositoryImpl(
    private val pokeApiService: PokeApiService,
    private val pokemonDao: PokemonDao,
    private val pokemonDetailsDao: PokemonDetailsDao
) : PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): Result<List<Pokemon>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val cachedPokemon = pokemonDao.getPokemonList(limit, offset)

                if (cachedPokemon.isNotEmpty()) {
                    cachedPokemon.toDomain()
                } else {
                    fetchAndCachePokemonList(limit, offset).getOrThrow()
                }
            }
        }
    }

    override suspend fun refreshPokemonList(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                pokemonDao.clearAll()
                fetchAndCachePokemonList(limit = 20, offset = 0).getOrThrow()
                Unit
            }
        }
    }

    private suspend fun fetchAndCachePokemonList(
        limit: Int,
        offset: Int
    ): Result<List<Pokemon>> {
        return runCatching {
            val response = pokeApiService.getPokemonList(limit, offset)
            val entities = response.results.mapNotNull { it.toEntity() }
            pokemonDao.insertAll(entities)
            entities.toDomain()
        }
    }


    override suspend fun getPokemonDetails(nameOrId: String): Result<PokemonDetails> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val cachedDetails = nameOrId.toIntOrNull()?.let { pokemonId ->
                    pokemonDetailsDao.getDetailsById(pokemonId)?.toDomain()
                }

                cachedDetails?.let {
                    Result.success(cachedDetails)
                } ?: fetchAndCacheDetails(nameOrId)

                val detailsDto = pokeApiService.getPokemonDetails(nameOrId)
                detailsDto.toDomain()
            }
        }
    }

    private suspend fun fetchAndCacheDetails(nameOrId: String): Result<PokemonDetails> {
        return runCatching {
            val detailsDto = pokeApiService.getPokemonDetails(nameOrId)
            val detailsEntity = detailsDto.toEntity()
            pokemonDetailsDao.insertDetails(detailsEntity)
            detailsDto.toDomain()
        }
    }

}