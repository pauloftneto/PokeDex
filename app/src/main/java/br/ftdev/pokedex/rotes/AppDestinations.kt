package br.ftdev.pokedex.rotes

object AppDestinations {
    const val POKEDEX_ROUTE = "pokedex"
    const val POKEMON_DETAILS = "pokemon_details/{pokemonId}"

    fun pokemonDetails(pokemonId: Int) = "pokemon_details/$pokemonId"
}