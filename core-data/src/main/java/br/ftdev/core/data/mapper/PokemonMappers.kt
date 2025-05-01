package br.ftdev.core.data.mapper

import br.ftdev.core.data.local.entity.PokemonDetailsEntity
import br.ftdev.core.data.local.entity.PokemonEntity
import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListItemResponse
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val POKEMON_IMAGE_URL = "https://raw.githubusercontent.com/PokeAPI/" +
        "sprites/master/sprites/pokemon/other/official-artwork/"

private val jsonParser = Json { ignoreUnknownKeys = true }

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

internal fun PokemonDetailsEntity.toDomain(): PokemonDetails? {
    return jsonParser.runCatching {
        val domainTypes = decodeFromString<List<PokemonType>>(typesJson)
        val domainStats = decodeFromString<List<PokemonStat>>(statsJson)

        PokemonDetails(
            id = id,
            name = name.replaceFirstChar { it.titlecase() },
            imageUrl = imageUrl,
            height = height,
            weight = weight,
            types = domainTypes,
            stats = domainStats
        )
    }.getOrNull()
}


internal fun PokemonDetailsResponse.toEntity(): PokemonDetailsEntity {
    val domainTypes = types.map { PokemonType(it.type.name.replaceFirstChar { char -> char.titlecase() }) }
    val domainStats = stats.map {
        PokemonStat(
            name = it.stat.name.replace('-', ' ')
                .split(' ')
                .joinToString(" ") { word -> word.replaceFirstChar { char -> char.titlecase() } },
            baseStat = it.baseStat
        )
    }

    return PokemonDetailsEntity(
        id = id,
        name = name,
        imageUrl = sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault,
        height = height / 10.0f,
        weight = weight / 10.0f,
        typesJson = jsonParser.encodeToString(domainTypes),
        statsJson = jsonParser.encodeToString(domainStats)
    )
}

internal fun PokemonDetailsResponse.toDomain(): PokemonDetails {
    val domainTypes = types.map { PokemonType(it.type.name.replaceFirstChar { char -> char.titlecase() }) }
    val domainStats = stats.map {
        PokemonStat(
            name = it.stat.name.replace('-', ' ')
                .split(' ')
                .joinToString(" ") { word -> word.replaceFirstChar { char -> char.titlecase() } },
            baseStat = it.baseStat
        )
    }
    return PokemonDetails(
        id = id,
        name = name.replaceFirstChar { it.titlecase() },
        imageUrl = sprites.other?.officialArtwork?.frontDefault ?: sprites.frontDefault,
        height = height / 10.0f,
        weight = weight / 10.0f,
        types = domainTypes,
        stats = domainStats
    )
}