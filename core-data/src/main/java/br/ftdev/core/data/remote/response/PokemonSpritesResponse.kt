package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PokemonSpritesResponse(
    @SerialName("front_default") val frontDefault: String?,
    @SerialName("other") val other: OtherSpritesResponse?
)
