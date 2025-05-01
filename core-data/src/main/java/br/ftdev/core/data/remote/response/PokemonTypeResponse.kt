package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PokemonTypeResponse(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String
)
