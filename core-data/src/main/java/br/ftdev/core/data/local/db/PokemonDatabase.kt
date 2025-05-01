package br.ftdev.core.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import br.ftdev.core.data.local.dao.PokemonDao
import br.ftdev.core.data.local.entity.PokemonEntity

@Database(
    entities = [PokemonEntity::class],
    version = 1,
    exportSchema = false
)
internal abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}