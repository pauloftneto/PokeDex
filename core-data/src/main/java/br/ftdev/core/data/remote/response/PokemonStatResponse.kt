package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PokemonStatResponse(
    @SerialName("stat") val stat: StatInfoResponse,
    @SerialName("base_stat") val baseStat: Int,
    @SerialName("effort") val effort: Int
)
