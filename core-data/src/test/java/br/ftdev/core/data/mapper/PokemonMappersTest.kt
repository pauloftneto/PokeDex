package br.ftdev.core.data.mapper

import br.ftdev.core.data.local.entity.PokemonDetailsEntity
import br.ftdev.core.data.local.entity.PokemonEntity
import br.ftdev.core.data.remote.response.PokemonListItemResponse
import br.ftdev.core.domain.model.Pokemon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PokemonMappersTest {

    @Test
    fun `extrai id da url com barra final`() {
        assertEquals(1, extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/1/"))
    }

    @Test
    fun `extrai id da url sem barra final`() {
        assertEquals(1, extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/1"))
    }

    @Test
    fun `retorna nulo quando id nao for numero`() {
        assertNull(extractIdFromUrl("https://pokeapi.co/api/v2/pokemon/abc/"))
    }

    @Test
    fun `mapeia PokemonListItemResponse para entity corretamente`() {
        val resp = PokemonListItemResponse(
            name = "bulbasaur",
            url = "https://pokeapi.co/api/v2/pokemon/1/"
        )
        val entity = resp.toEntity()
        assertNotNull(entity)
        assertEquals(1, entity!!.id)
        assertEquals("bulbasaur", entity.name)
        assertTrue(entity.imageUrl?.endsWith("1.png") == true)
    }

    @Test
    fun `retorna nulo quando toEntity falha no id`() {
        val resp = PokemonListItemResponse(name = "pikachu", url = "invalid")
        assertNull(resp.toEntity())
    }

    @Test
    fun `converte entity para domain`() {
        val entity = PokemonEntity(id = 2, name = "ivysaur", imageUrl = "url.png")
        val domain = entity.toDomain()
        assertEquals(Pokemon(2, "Ivysaur", "url.png"), domain)
    }

    @Test
    fun `converte lista de entity para lista de domain`() {
        val list = listOf(
            PokemonEntity(1, "charmander", "u"),
            PokemonEntity(2, "bulbasaur", "u2")
        )
        val domains = list.toDomain()
        assertEquals(
            listOf(
                Pokemon(1, "Charmander", "u"),
                Pokemon(2, "Bulbasaur", "u2")
            ),
            domains
        )
    }

    @Test
    fun `converte PokemonDetailsEntity para domain`() {
        val typesJson = """[{"slot":1,"type":{"name":"Fire","url":""}}]"""
        val statsJson = """[{"stat":{"name":"Hp","url":""},"base_stat":45}]"""
        val entity = PokemonDetailsEntity(
            id = 1,
            name = "charizard",
            imageUrl = "img.png",
            height = 10f,
            weight = 20f,
            typesJson = typesJson,
            statsJson = statsJson
        )
        val details = entity.toDomain()
        assertNotNull(details)
        details.let { d ->
            assertEquals(1, d.id)
            assertEquals("Charizard", d.name)
            assertEquals("img.png", d.imageUrl)
            assertEquals(10f, d.height)
            assertEquals(20f, d.weight)
        }
    }

    @Test
    fun `retorna nulo quando parsing de detalhes falha`() {
        val bad = PokemonDetailsEntity(
            id = 1,
            name = "x",
            imageUrl = null,
            height = 0f,
            weight = 0f,
            typesJson = "not json",
            statsJson = "not json"
        )
        val details = bad.toDomain()
        assertNotNull(details)
        assertEquals(1, details.id)
        assertEquals("X", details.name)
        assertNull(details.imageUrl)
        assertEquals(0f, details.height)
        assertEquals(0f, details.weight)
        assertTrue(details.types.isEmpty())
        assertTrue(details.stats.isEmpty())
    }

}