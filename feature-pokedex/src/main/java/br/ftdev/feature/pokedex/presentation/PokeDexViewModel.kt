package br.ftdev.feature.pokedex.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.usecase.GetPokemonListUseCase
import br.ftdev.feature.pokedex.presentation.event.PokeDexUiEvent
import br.ftdev.feature.pokedex.presentation.state.PokedexUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokeDexViewModel(
    private val getPokemonListUseCase: GetPokemonListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokedexUiState>(PokedexUiState.Loading)
    val uiState: StateFlow<PokedexUiState> = _uiState.asStateFlow()

    private val _eventChannel = MutableSharedFlow<PokeDexUiEvent>(replay = 0)
    val eventFlow: SharedFlow<PokeDexUiEvent> = _eventChannel.asSharedFlow()

    private var currentPage = 0
    private val pokemonList = mutableListOf<Pokemon>()
    private var fetchJob: Job? = null
    private var canLoadMore = true // Assume que pode carregar mais inicialmente

    companion object {
        private const val PAGE_SIZE = 20 // Quantos Pokémon buscar por vez
    }

    init {
        fetchPokemonList() // Carrega a primeira página ao iniciar
    }

    fun fetchPokemonList(forceRefresh: Boolean = false) {
        // Evita múltiplas chamadas concorrentes e não busca se não puder carregar mais
        if (fetchJob?.isActive == true || (!canLoadMore && !forceRefresh)) {
            return
        }

        if (forceRefresh) {
            currentPage = 0
            pokemonList.clear()
            canLoadMore = true
            _uiState.value = PokedexUiState.Loading
        }

        // Se já temos itens e estamos carregando mais, mostramos o estado atual com um loading sutil
        // (implementação do loading sutil pode ser feita na UI)
        // Se for o primeiro carregamento ou refresh, _uiState já é Loading

        fetchJob = viewModelScope.launch {

            // Nota: A UI precisa interpretar isso para mostrar um loading diferente (ex: no final da lista)
            // Poderíamos ter um estado LoadingMore explícito se quisermos mais controle.
            if (!forceRefresh && pokemonList.isNotEmpty()) {
                // Opcional: Atualizar o estado para indicar loading more visualmente
                // _uiState.value = PokedexUiState.Success(pokemonList.toList(), canLoadMore, isLoadingMore = true)
                // Isso requer adicionar `isLoadingMore` ao PokedexUiState.Success
            }

            getPokemonListUseCase(limit = PAGE_SIZE, offset = currentPage * PAGE_SIZE)
                .collect { result ->
                    result.onSuccess { newPokemon ->
                        canLoadMore = newPokemon.size == PAGE_SIZE // Se veio menos que o limite, não há mais
                        pokemonList.addAll(newPokemon)
                        currentPage++
                        _uiState.value = PokedexUiState.Success(
                            pokemonList = pokemonList.toList(), // Envia cópia da lista
                            canLoadMore = canLoadMore
                        )
                    }.onFailure { exception ->
                        // Se for o primeiro load e falhar, mostra erro geral
                        // Se for carregando mais e falhar, pode mostrar um snackbar/toast na UI
                        // e manter o estado Success anterior
                        val errorMsg = exception.message ?: "Erro desconhecido ao buscar Pokémon"
                        if (pokemonList.isEmpty()) {
                            // Erro no carregamento inicial, atualiza o estado principal
                            _uiState.value = PokedexUiState.Error(errorMsg)
                            canLoadMore = false // Impede futuras tentativas se o inicial falhar
                        }else {
                            // Erro ao carregar MAIS, emite evento e mantém estado Success
                            _eventChannel.emit(PokeDexUiEvent.ShowSnackbar(errorMsg))
                            // Mantém o estado Success com a lista atual
                            _uiState.value = PokedexUiState.Success(
                                pokemonList = pokemonList.toList(),
                                canLoadMore = canLoadMore // Talvez setar para false até um retry manual?
                                // isLoadingMore = false // Resetar flag se adicionada
                            )
                        }
                        // Logar sempre é bom
                        println("Erro ao carregar Pokémon: $exception")
                    }
                }
        }
    }
}