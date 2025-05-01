package br.ftdev.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.ftdev.core.data.local.converters.PokemonTypeConverters
import br.ftdev.core.data.local.dao.PokemonDao
import br.ftdev.core.data.local.dao.PokemonDetailsDao
import br.ftdev.core.data.local.entity.PokemonDetailsEntity
import br.ftdev.core.data.local.entity.PokemonEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonDetailsEntity::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(PokemonTypeConverters::class)
internal abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailsDao(): PokemonDetailsDao
}