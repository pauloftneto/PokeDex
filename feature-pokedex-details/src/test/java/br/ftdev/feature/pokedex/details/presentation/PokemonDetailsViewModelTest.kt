package br.ftdev.feature.pokedex.details.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import br.ftdev.core.domain.usecase.GetPokemonDetailsUseCase
import br.ftdev.feature.pokedex.details.presentation.state.PokemonDetailsUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonDetailsViewModelTest {

    private val getPokemonDetailsUseCase = mockk<GetPokemonDetailsUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: PokemonDetailsViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deve emitir Loading e depois Success quando o useCase retornar sucesso`() = runTest {
        val details = PokemonDetails(
            id = 1,
            name = "Bulbasaur",
            imageUrl = "https://imagem.com/bulbasaur.png",
            height = 0.7f,
            weight = 6.9f,
            types = listOf(PokemonType("grass"), PokemonType("poison")),
            stats = listOf(PokemonStat("hp", 45), PokemonStat("attack", 49))
        )

        coEvery { getPokemonDetailsUseCase(any()) } returns flow {
            emit(Result.success(details))
        }

        viewModel = PokemonDetailsViewModel(
            getPokemonDetailsUseCase,
            SavedStateHandle(mapOf("pokemonId" to 1))
        )

        viewModel.uiState.test {
            assertEquals(PokemonDetailsUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PokemonDetailsUiState.Success(details), awaitItem())
        }
    }

    @Test
    fun `deve emitir Loading e depois Error quando o useCase retornar falha`() = runTest {
        val errorMessage = "Erro de rede"

        coEvery { getPokemonDetailsUseCase(any()) } returns flow {
            emit(Result.failure(Throwable(errorMessage)))
        }

        viewModel = PokemonDetailsViewModel(
            getPokemonDetailsUseCase,
            SavedStateHandle(mapOf("pokemonId" to 25))
        )

        viewModel.uiState.test {
            assertEquals(PokemonDetailsUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(PokemonDetailsUiState.Error(errorMessage), awaitItem())
        }
    }

    @Test
    fun `deve emitir mensagem de erro padrão quando exceção não tem mensagem`() = runTest {
        coEvery { getPokemonDetailsUseCase(any()) } returns flow {
            emit(Result.failure(Throwable()))
        }

        viewModel = PokemonDetailsViewModel(
            getPokemonDetailsUseCase,
            SavedStateHandle(mapOf("pokemonId" to 99))
        )

        viewModel.uiState.test {
            assertEquals(PokemonDetailsUiState.Loading, awaitItem())
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(
                PokemonDetailsUiState.Error("Erro desconhecido ao buscar Pokémon"),
                awaitItem()
            )
        }
    }
}