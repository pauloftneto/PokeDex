package br.ftdev.core.domain.usecase

import app.cash.turbine.test
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPokemonListUseCaseTest {

    private val repository: PokemonRepository = mockk()
    private lateinit var useCase: GetPokemonListUseCase

    @Before
    fun setup() {
        useCase = GetPokemonListUseCase(repository)
    }

    @Test
    fun `invoke deve emitir sucesso quando repository retornar success`() = runTest {
        val limit = 10
        val offset = 5
        val pokemonList = listOf(Pokemon(id = 1, name = "Bulbasaur", imageUrl = "url"))
        coEvery { repository.getPokemonList(limit, offset) } returns Result.success(pokemonList)

        useCase(limit, offset).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(pokemonList, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `invoke deve emitir falha quando repository retornar failure`() = runTest {
        val limit = 10
        val offset = 5
        val error = Throwable("Erro")
        coEvery { repository.getPokemonList(limit, offset) } returns Result.failure(error)

        useCase(limit, offset).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            awaitComplete()
        }
    }
}