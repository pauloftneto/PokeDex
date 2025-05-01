package br.ftdev.core.domain.mapper

import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.data.remote.response.PokemonListItemResponse
import br.ftdev.core.data.remote.response.PokemonStatResponse
import br.ftdev.core.data.remote.response.PokemonTypeResponse
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType

private const val POKEMON_IMAGE_URL = "https://raw.githubusercontent.com/PokeAPI/" +
        "sprites/master/sprites/pokemon/other/official-artwork/"

fun extractIdFromUrl(url: String): Int? {
    return url.trimEnd('/').substringAfterLast('/').toIntOrNull()
}

fun PokemonListItemResponse.toDomain(): Pokemon? {
    val pokemonId = extractIdFromUrl(url) ?: return null
    val imageUrl =
        POKEMON_IMAGE_URL.plus("${pokemonId}.png")
    return Pokemon(
        id = pokemonId,
        name = name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        },
        imageUrl = imageUrl
    )
}

fun PokemonDetailsResponse.toDomain(): PokemonDetails {
    return PokemonDetails(
        id = id,
        name = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
        imageUrl = sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault,
        height = height / 10.0f,
        weight = weight / 10.0f,
        types = types.map { it.type.toDomain() },
        stats = stats.map { it.toDomain() }
    )
}

fun PokemonTypeResponse.toDomain(): PokemonType {
    return PokemonType(
        name = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    )
}

fun PokemonStatResponse.toDomain(): PokemonStat {
    return PokemonStat(
        name = stat.name.replace('-', ' ')
            .split(' ')
            .joinToString(" ") { word ->
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            },
        baseStat = baseStat
    )
}