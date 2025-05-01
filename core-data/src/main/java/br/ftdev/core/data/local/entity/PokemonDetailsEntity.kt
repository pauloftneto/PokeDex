package br.ftdev.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_details")
internal data class PokemonDetailsEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Float,
    val weight: Float,
    val typesJson: String,
    val statsJson: String
)