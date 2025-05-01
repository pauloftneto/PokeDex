// Mappers.kt
package br.ftdev.core.data.mapper

import br.ftdev.core.data.local.entity.PokemonEntity
import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListItemResponse
import br.ftdev.core.data.remote.response.PokemonStatResponse
import br.ftdev.core.data.remote.response.PokemonTypeResponse
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType

private const val POKEMON_IMAGE_URL = "https://raw.githubusercontent.com/PokeAPI/" +
        "sprites/master/sprites/pokemon/other/official-artwork/"

internal fun extractIdFromUrl(url: String): Int? {
    return url.trimEnd('/').substringAfterLast('/').toIntOrNull()
}

internal fun PokemonListItemResponse.toEntity(): PokemonEntity? {
    val pokemonId = extractIdFromUrl(url) ?: return null
    val imageUrl = POKEMON_IMAGE_URL.plus("${pokemonId}.png")
    return PokemonEntity(
        id = pokemonId,
        name = name,
        imageUrl = imageUrl
    )
}

internal fun PokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        },
        imageUrl = imageUrl
    )
}

internal fun List<PokemonEntity>.toDomain(): List<Pokemon> {
    return this.map { it.toDomain() }
}

internal fun PokemonDetailsResponse.toDomain(): PokemonDetails {
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

private fun PokemonTypeResponse.toDomain(): PokemonType {
    return PokemonType(
        name = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    )
}

private fun PokemonStatResponse.toDomain(): PokemonStat {
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