package br.ftdev.feature.pokedex.presentation

import app.cash.turbine.test
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.usecase.GetPokemonListUseCase
import br.ftdev.core.domain.usecase.RefreshPokemonListUseCase
import br.ftdev.feature.pokedex.presentation.event.PokeDexUiEvent
import br.ftdev.feature.pokedex.presentation.state.PokedexUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokeDexViewModelTest {

    private val getPokemonListUseCase = mockk<GetPokemonListUseCase>()
    private val refreshPokemonListUseCase = mockk<RefreshPokemonListUseCase>()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: PokeDexViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `deve emitir Loading e depois Success ao buscar lista com sucesso`() = runTest {
        val list = listOf(Pokemon(1, "Bulbasaur", ""), Pokemon(2, "Ivysaur", ""))
        coEvery { getPokemonListUseCase(any(), any()) } returns flow {
            emit(Result.success(list))
        }

        viewModel = PokeDexViewModel(getPokemonListUseCase, refreshPokemonListUseCase)

        viewModel.uiState.test {
            val loading = awaitItem()
            assertEquals(PokedexUiState.Loading(isInitialLoading = true), loading)

            this@runTest.advanceUntilIdle()

            val success = awaitItem()
            assertEquals(PokedexUiState.Success(list, canLoadMore = false), success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deve emitir Error se a lista falhar e não tiver dados anteriores`() = runTest {
        coEvery { getPokemonListUseCase(any(), any()) } returns flow {
            emit(Result.failure(Throwable("Falha na API")))
        }

        viewModel = PokeDexViewModel(getPokemonListUseCase, refreshPokemonListUseCase)

        viewModel.uiState.test {
            val loading = awaitItem()
            assertEquals(PokedexUiState.Loading(isInitialLoading = true), loading)
            this@runTest.advanceUntilIdle()
            val error = awaitItem()
            assertEquals(PokedexUiState.Error("Falha na API"), error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deve buscar novamente ao forçar refresh e atualizar lista`() = runTest {
        val list = listOf(Pokemon(1, "Bulbasaur", ""), Pokemon(2, "Ivysaur", ""))
        coEvery { refreshPokemonListUseCase() } returns flowOf(Result.success(Unit))
        coEvery { getPokemonListUseCase(any(), any()) } returns flow {
            emit(Result.success(list))
        }

        viewModel = PokeDexViewModel(getPokemonListUseCase, refreshPokemonListUseCase)

        viewModel.refreshList()

        viewModel.uiState.test {
            val loadingInitial = awaitItem()
            assertEquals(PokedexUiState.Loading(isInitialLoading = true), loadingInitial)

            val next = awaitItem()
            assert(next is PokedexUiState.Success)
            assertEquals(PokedexUiState.Success(list, canLoadMore = false), next)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deve emitir evento de erro ao falhar no refresh`() = runTest {
        coEvery { refreshPokemonListUseCase() } returns flowOf(Result.failure(Throwable("Erro no refresh")))
        coEvery { getPokemonListUseCase(any(), any()) } returns flowOf(Result.success(emptyList()))

        viewModel = PokeDexViewModel(getPokemonListUseCase, refreshPokemonListUseCase)

        viewModel.eventFlow.test {
            viewModel.refreshList()
            this@runTest.advanceUntilIdle()

            assertEquals(PokeDexUiEvent.ShowSnackbar("Erro no refresh"), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deve emitir Success com lista vazia quando getPokemonListUseCase retornar vazio`() =
        runTest {
            coEvery { getPokemonListUseCase(any(), any()) } returns flowOf(
                Result.success(
                    emptyList()
                )
            )

            viewModel = PokeDexViewModel(getPokemonListUseCase, refreshPokemonListUseCase)

            viewModel.uiState.test {
                awaitItem()
                val success = awaitItem()
                assertEquals(PokedexUiState.Success(emptyList(), canLoadMore = false), success)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `deve emitir sucesso após erro inicial ao forçar refresh`() = runTest {
        val list = listOf(Pokemon(1, "Bulbasaur", ""))

        coEvery { getPokemonListUseCase(any(), any()) } returnsMany listOf(
            flowOf(Result.failure(Throwable("Erro inicial"))),
            flowOf(Result.success(list))
        )
        coEvery { refreshPokemonListUseCase() } returns flowOf(Result.success(Unit))

        viewModel = PokeDexViewModel(getPokemonListUseCase, refreshPokemonListUseCase)

        viewModel.uiState.test {
            awaitItem()
            val error = awaitItem()
            assertEquals(PokedexUiState.Error("Erro inicial"), error)

            viewModel.refreshList()
            advanceUntilIdle()

            val loading = awaitItem()
            assertEquals(PokedexUiState.Loading(isInitialLoading = false), loading)

            val success = awaitItem()
            assertEquals(PokedexUiState.Success(list, canLoadMore = false), success)

            cancelAndIgnoreRemainingEvents()
        }
    }
}