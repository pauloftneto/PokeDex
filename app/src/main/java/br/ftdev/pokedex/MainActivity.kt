package br.ftdev.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.feature.pokedex.presentation.screen.PokedexScreen
import br.ftdev.pokedex.rotes.AppDestinations

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokemonAppTheme {
                AppNavigation()
            }
        }
    }


    @Composable
    fun AppNavigation() {
        val navController = rememberNavController() // Cria o controlador de navegação

        NavHost(
            navController = navController,
            startDestination = AppDestinations.POKEDEX_ROUTE // Define a tela inicial
        ) {
            // Define a tela associada à rota POKEDEX_ROUTE
            composable(route = AppDestinations.POKEDEX_ROUTE) {
                PokedexScreen(
                    // No futuro, passe o navController para a PokedexScreen
                    // para que ela possa navegar para os detalhes, por exemplo:
                    // onPokemonClick = { pokemonId ->
                    //    navController.navigate("${AppDestinations.POKEMON_DETAIL_ROUTE.replace("{pokemonId}", pokemonId.toString())}")
                    // }
                )
            }

            // Adicione outras telas/destinos aqui
            /*
            composable(
                route = AppDestinations.POKEMON_DETAIL_ROUTE,
                arguments = listOf(navArgument("pokemonId") { type = NavType.IntType })
            ) { backStackEntry ->
                val pokemonId = backStackEntry.arguments?.getInt("pokemonId")
                if (pokemonId != null) {
                    // Chame a tela de detalhes aqui, passando o ID
                    // PokemonDetailScreen(pokemonId = pokemonId, navController = navController)
                } else {
                    // Lidar com erro de ID não encontrado
                }
            }
            */
        }
    }
}