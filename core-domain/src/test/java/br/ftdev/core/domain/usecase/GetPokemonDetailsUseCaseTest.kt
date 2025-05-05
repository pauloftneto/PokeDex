package br.ftdev.core.domain.usecase

import app.cash.turbine.test
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import br.ftdev.core.domain.repository.PokemonRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetPokemonDetailsUseCaseTest {

    private lateinit var repository: PokemonRepository
    private lateinit var useCase: GetPokemonDetailsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetPokemonDetailsUseCase(repository)
    }

    @Test
    fun `invoke should emit success when repository returns success`() = runTest {
        val expected =  PokemonDetails(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "https://imagem.com/bulbasaur.png",
            height = 0.7f,
            weight = 6.9f,
            types = listOf(PokemonType("grass"), PokemonType("poison")),
            stats = listOf(PokemonStat("hp", 45), PokemonStat("attack", 49))
        )
        coEvery { repository.getPokemonDetails("42") } returns Result.success(expected)

        useCase("42").test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(expected, result.getOrNull())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit failure when repository returns failure`() = runTest {
        val error = IllegalStateException("not found")
        coEvery { repository.getPokemonDetails("missing") } returns Result.failure(error)

        useCase("missing").test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals(error, result.exceptionOrNull())
            awaitComplete()
        }
    }
}