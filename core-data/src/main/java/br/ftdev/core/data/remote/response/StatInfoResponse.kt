package br.ftdev.core.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatInfoResponse(
    @SerialName("name") val name: String,
    @SerialName("url") val url: String

)
