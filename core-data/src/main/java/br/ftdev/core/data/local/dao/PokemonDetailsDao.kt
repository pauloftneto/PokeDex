package br.ftdev.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import br.ftdev.core.data.local.entity.PokemonDetailsEntity

@Dao
internal interface PokemonDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetails(details: PokemonDetailsEntity)

    @Query("SELECT * FROM pokemon_details WHERE id = :id")
    suspend fun getDetailsById(id: Int): PokemonDetailsEntity?

    @Query("DELETE FROM pokemon_details")
    suspend fun clearAllDetails()

}