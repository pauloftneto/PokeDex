package br.ftdev.core.domain.usecase

import br.ftdev.core.domain.repository.PokemonRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class RefreshPokemonListUseCaseTest {

    private lateinit var mockRepository: PokemonRepository
    private lateinit var refreshPokemonListUseCase: RefreshPokemonListUseCase

    @Before
    fun setUp() {
        mockRepository = mockk()
        refreshPokemonListUseCase = RefreshPokemonListUseCase(mockRepository)
    }

    @Test
    fun `invoke should call repository refreshPokemonList and return success on success`() =
        runTest {
            val successResult = Result.success(Unit)
            coEvery { mockRepository.refreshPokemonList() } returns successResult

            val result = refreshPokemonListUseCase().first()

            assertThat(result.isSuccess).isTrue()

            coVerify(exactly = 1) { mockRepository.refreshPokemonList() }
            confirmVerified(mockRepository)
        }

    @Test
    fun `invoke should call repository refreshPokemonList and return failure on failure`() =
        runTest {
            val fakeException = IOException("Failed to refresh source")
            val failureResult = Result.failure<Unit>(fakeException)
            coEvery { mockRepository.refreshPokemonList() } returns failureResult

            val result = refreshPokemonListUseCase().first()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(fakeException)

            coVerify(exactly = 1) { mockRepository.refreshPokemonList() }
            confirmVerified(mockRepository)
        }
}