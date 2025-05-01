package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PokemonTypeSlotResponse(
    @SerialName("slot") val slot: Int,
    @SerialName("type") val type: PokemonTypeResponse
)
