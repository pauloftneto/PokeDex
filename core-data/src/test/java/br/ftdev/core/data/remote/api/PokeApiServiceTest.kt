package br.ftdev.core.data.remote.api

import br.ftdev.core.data.remote.response.PokemonDetailsResponse
import br.ftdev.core.data.remote.response.PokemonListResponse
import com.google.common.truth.Truth.assertThat
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import junit.framework.TestCase.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class PokeApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: PokeApiService
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var json: Json

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()

        json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(PokeApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getPokemonList success should send correct request and parse response`() = runTest {
        val limit = 15
        val offset = 30
        val expectedPath = "/pokemon?limit=$limit&offset=$offset"

        val fakeJsonResponse = """
            {
                "count": 1000,
                "next": "some_next_url",
                "previous": "some_prev_url",
                "results": [
                    {"name": "bulbasaur", "url": "url1"},
                    {"name": "ivysaur", "url": "url2"}
                ]
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(fakeJsonResponse).setResponseCode(200))

        val response: PokemonListResponse = service.getPokemonList(limit, offset)

        val recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertThat(recordedRequest.toString()).isNotNull()
        assertThat(recordedRequest!!.method).isEqualTo("GET")
        assertThat(recordedRequest.path).isEqualTo(expectedPath)

        assertThat(response.count).isEqualTo(1000)
        assertThat(response.next).isEqualTo("some_next_url")
        assertThat(response.results).hasSize(2)
        assertThat(response.results[0].name).isEqualTo("bulbasaur")
        assertThat(response.results[1].url).isEqualTo("url2")
    }

    @Test
    fun `getPokemonDetails success should send correct request and parse response`() = runTest {
        val pokemonName = "pikachu"
        val expectedPath = "/pokemon/$pokemonName"
        val fakeJsonResponse = """
            {
                "id": 25,
                "name": "pikachu",
                "height": 4,
                "weight": 60,
                "types": [{"slot": 1, "type": {"name": "electric", "url": ""}}],
                "sprites": {
                     "front_default": "front.png",
                     "other": {
                        "official-artwork": {
                            "front_default": "official.png"
                        }
                     }
                },
                 "stats": [{"base_stat": 35, "effort": 0, "stat": {"name": "hp", "url": ""}}],
                 "abilities": [{"ability": {"name": "static", "url": ""}, "is_hidden": false, "slot": 1}]
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(fakeJsonResponse).setResponseCode(200))

        val response: PokemonDetailsResponse = service.getPokemonDetails(pokemonName)

        val recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertThat(recordedRequest).isNotNull()
        assertThat(recordedRequest!!.method).isEqualTo("GET")
        assertThat(recordedRequest.path).isEqualTo(expectedPath)

        assertThat(response.id).isEqualTo(25)
        assertThat(response.name).isEqualTo("pikachu")
        assertThat(response.height).isEqualTo(4)
        assertThat(response.types).hasSize(1)
        assertThat(response.types[0].type.name).isEqualTo("electric")
        assertThat(response.sprites.other?.officialArtwork?.frontDefault).isEqualTo("official.png")
        assertThat(response.stats).hasSize(1)
        assertThat(response.stats[0].stat.name).isEqualTo("hp")
    }

    @Test
    fun `getPokemonDetails failure (404) should throw HttpException`() = runTest {
        val pokemonName = "mewthree"
        val expectedPath = "/pokemon/$pokemonName"

        mockWebServer.enqueue(MockResponse().setResponseCode(404).setBody("Not Found"))

        try {
            service.getPokemonDetails(pokemonName)
            fail("Expected HttpException was not thrown")
        } catch (e: HttpException) {
            assertThat(e.code()).isEqualTo(404)
        }

        val recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertThat(recordedRequest).isNotNull()
        assertThat(recordedRequest!!.path).isEqualTo(expectedPath)
    }

    @Test
    fun `getPokemonList failure (500) should throw HttpException`() = runTest {
        val expectedPath = "/pokemon?limit=20&offset=0"
        mockWebServer.enqueue(MockResponse().setResponseCode(500).setBody("Server Error"))

        var caughtException: HttpException? = null
        try {
            service.getPokemonList()
        } catch (e: HttpException) {
            caughtException = e
        }

        assertThat(caughtException).isNotNull()
        assertThat(caughtException!!.code()).isEqualTo(500)

        val recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS)
        assertThat(recordedRequest).isNotNull()
        assertThat(recordedRequest!!.path).isEqualTo(expectedPath)
    }
}