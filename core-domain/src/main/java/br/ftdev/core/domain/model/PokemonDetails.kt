package br.ftdev.core.domain.model

data class PokemonDetails(
    val id: Int,
    val name: String,
    val imageUrl: String?,
    val height: Float,
    val weight: Float,
    val types: List<PokemonType>,
    val stats: List<PokemonStat>
)
