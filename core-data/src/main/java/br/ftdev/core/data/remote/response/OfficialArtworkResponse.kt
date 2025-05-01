package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OfficialArtworkResponse(
    @SerialName("front_default") val frontDefault: String?
)
