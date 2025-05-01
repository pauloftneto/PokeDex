package br.ftdev.core.data.local.converters

import androidx.room.TypeConverter
import br.ftdev.core.domain.model.PokemonStat
import br.ftdev.core.domain.model.PokemonType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal object PokemonTypeConverters {

    private val jsonParser = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromPokemonTypeList(types: List<PokemonType>?): String {
        return types?.let { jsonParser.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toPokemonTypeList(json: String): List<PokemonType>? {
        return runCatching { jsonParser.decodeFromString<List<PokemonType>>(json) }.getOrNull()
    }

    @TypeConverter
    fun fromPokemonStatList(stats: List<PokemonStat>?): String {
        return stats?.let { jsonParser.encodeToString(it) } ?: "[]"
    }

    @TypeConverter
    fun toPokemonStatList(json: String): List<PokemonStat>? {
        return runCatching { jsonParser.decodeFromString<List<PokemonStat>>(json) }.getOrNull()
    }
}