package br.ftdev.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.ftdev.core.ui.theme.PokemonAppTheme
import br.ftdev.feature.pokedex.details.presentation.screen.PokemonDetailsScreen
import br.ftdev.feature.pokedex.presentation.screen.PokedexScreen
import br.ftdev.pokedex.rotes.AppDestinations

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PokemonAppTheme {
                AppNavigation()
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = AppDestinations.POKEDEX_ROUTE) {
            composable(AppDestinations.POKEDEX_ROUTE) {
                PokedexScreen(
                    onPokemonClick = { pokemonId ->
                        navController.navigate(AppDestinations.pokemonDetails(pokemonId))
                    }
                )
            }

            composable(
                route = AppDestinations.POKEMON_DETAILS,
                arguments = listOf(navArgument("pokemonId") { type = NavType.IntType })
            ) {
                PokemonDetailsScreen(navController = navController)
            }
        }
    }
}