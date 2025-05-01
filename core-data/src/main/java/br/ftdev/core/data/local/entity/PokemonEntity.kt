package br.ftdev.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon")
internal data class PokemonEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?
)