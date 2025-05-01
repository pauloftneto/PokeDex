package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonDetailsResponse(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String,
    @SerialName("height") val height: Int,
    @SerialName("weight") val weight: Int,
    @SerialName("types") val types: List<PokemonTypeSlotResponse>,
    @SerialName("sprites") val sprites: PokemonSpritesResponse,
    @SerialName("stats") val stats: List<PokemonStatResponse>
)
