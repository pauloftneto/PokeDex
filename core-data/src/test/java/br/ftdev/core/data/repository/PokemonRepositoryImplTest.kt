package br.ftdev.core.data.repository

import br.ftdev.core.analytics.EngineeringTracker
import br.ftdev.core.data.local.dao.PokemonDao
import br.ftdev.core.data.local.dao.PokemonDetailsDao
import br.ftdev.core.data.local.entity.PokemonEntity
import br.ftdev.core.data.mapper.PokemonMappers.toDomain
import br.ftdev.core.data.remote.api.PokeApiService
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonRepositoryImplTest {

    private lateinit var repository: PokemonRepositoryImpl
    private val pokeApiService: PokeApiService = mockk()
    private val pokemonDao: PokemonDao = mockk()
    private val pokemonDetailsDao: PokemonDetailsDao = mockk()
    private val engTrack: EngineeringTracker = mockk()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        repository = PokemonRepositoryImpl(pokeApiService, engTrack, pokemonDao, pokemonDetailsDao)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `listarPokemons retorna cache quando disponível`() = runTest {
        val pokemonEntity = PokemonEntity(1, "Bulbasaur", "")
        coEvery { pokemonDao.getPokemonList(10, 0) } returns listOf(pokemonEntity)

        val result = repository.getPokemonList(10, 0)

        assertTrue(result.isSuccess)
        assertEquals(pokemonEntity.toDomain(), result.getOrNull()?.first())
    }

    @Test
    fun `atualizarPokemons limpa dados locais`() = runTest {
        coEvery { pokemonDao.clearAll() } just Runs
        coEvery { pokemonDetailsDao.clearAllDetails() } just Runs

        val result = repository.refreshPokemonList()

        assertTrue(result.isSuccess)
        coVerify { pokemonDao.clearAll() }
        coVerify { pokemonDetailsDao.clearAllDetails() }
    }

    @Test
    fun `listarPokemons retorna falha quando API falha`() = runTest {
        coEvery { pokemonDao.getPokemonList(10, 0) } returns emptyList()
        coEvery { pokeApiService.getPokemonList(10, 0) } throws RuntimeException("API failure")

        val result = repository.getPokemonList(10, 0)

        assertTrue(result.isFailure)
    }

    @Test
    fun `detalhesPokemon retorna falha quando API falha`() = runTest {
        coEvery { pokemonDetailsDao.getDetailsById(1) } returns null
        coEvery { pokeApiService.getPokemonDetails("1") } throws RuntimeException("API failure")

        val result = repository.getPokemonDetails("1")

        assertTrue(result.isFailure)
    }

    @Test
    fun `atualizarPokemons retorna falha quando clear falha`() = runTest {
        coEvery { pokemonDao.clearAll() } throws RuntimeException("DB failure")
        coEvery { pokemonDetailsDao.clearAllDetails() } just Runs

        val result = repository.refreshPokemonList()

        assertTrue(result.isFailure)
    }

}
