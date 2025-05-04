package br.ftdev.core.data.mapper

import br.ftdev.core.analytics.EngineeringTracker
import br.ftdev.core.data.local.converters.PokemonTypeConverters.fromPokemonStatList
import br.ftdev.core.data.local.converters.PokemonTypeConverters.fromPokemonTypeList
import br.ftdev.core.data.local.converters.PokemonTypeConverters.toPokemonStatList
import br.ftdev.core.data.local.converters.PokemonTypeConverters.toPokemonTypeList
import br.ftdev.core.data.local.entity.PokemonDetailsEntity
import br.ftdev.core.data.local.entity.PokemonEntity
import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListItemResponse
import br.ftdev.core.domain.model.Pokemon
import br.ftdev.core.domain.model.PokemonDetails
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val POKEMON_IMAGE_URL = "https://raw.githubusercontent.com/PokeAPI/" +
        "sprites/master/sprites/pokemon/other/official-artwork/"

object PokemonMappers : KoinComponent {
    private val tracker: EngineeringTracker by inject()

    private fun extractIdFromUrl(url: String): Int? {
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

    private fun PokemonEntity.toDomain(): Pokemon {
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

    internal fun PokemonDetailsEntity.toDomain(): PokemonDetails {
        val domainTypes = runCatching { toPokemonTypeList(typesJson) }
            .getOrElse { e ->
                tracker.trackError(
                    event = "parse_entity_types_error",
                    error = e,
                    params = mapOf("entityId" to id.toString())
                )
                null
            }
        val domainStats = runCatching { toPokemonStatList(statsJson) }
            .getOrElse { e ->
                tracker.trackError(
                    event = "parse_entity_stats_error",
                    error = e,
                    params = mapOf("entityId" to id.toString())
                )
                null
            }

        return PokemonDetails(
            id = id,
            name = name.replaceFirstChar { it.titlecase() },
            imageUrl = imageUrl,
            height = height,
            weight = weight,
            types = domainTypes.orEmpty(),
            stats = domainStats.orEmpty()
        )
    }


    internal fun PokemonDetailsResponse.toEntity(): PokemonDetailsEntity {
        val domainTypes =
            types.map { PokemonType(it.type.name.replaceFirstChar { char -> char.titlecase() }) }
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
            typesJson = fromPokemonTypeList(domainTypes),
            statsJson = fromPokemonStatList(domainStats)
        )
    }

    internal fun PokemonDetailsResponse.toDomain(): PokemonDetails {
        val domainTypes = runCatching {
            types.map { PokemonType(it.type.name.replaceFirstChar { char -> char.titlecase() }) }
        }.getOrElse { e ->
            tracker.trackError(
                event = "parse_response_types_error",
                error = e,
                params = mapOf("responseId" to id.toString())
            )
            emptyList()
        }
        val domainStats = runCatching {
            stats.map {
                PokemonStat(
                    name = it.stat.name.replace('-', ' ')
                        .split(' ')
                        .joinToString(" ") { word -> word.replaceFirstChar { char -> char.titlecase() } },
                    baseStat = it.baseStat
                )
            }
        }.getOrElse { e ->
            tracker.trackError(
                event = "parse_response_stats_error",
                error = e,
                params = mapOf("responseId" to id.toString())
            )
            emptyList()
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
}