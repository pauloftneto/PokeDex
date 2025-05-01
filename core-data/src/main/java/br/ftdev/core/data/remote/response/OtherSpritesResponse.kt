package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OtherSpritesResponse(
    @SerialName("official-artwork") val officialArtwork: OfficialArtworkResponse?
)
