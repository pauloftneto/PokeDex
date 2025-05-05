package br.ftdev.core.data.di

import br.ftdev.core.data.remote.api.PokeApiService
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

class DataModuleTest : KoinTest {

    private val json: Json by inject()
    private val interceptor: HttpLoggingInterceptor by inject()
    private val client: OkHttpClient by inject()
    private val service: PokeApiService by inject()

    @Before
    fun setup() {
        startKoin {
            modules(dataModule)
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `deve injetar Json`() {
        assertNotNull(json)
    }

    @Test
    fun `deve injetar HttpLoggingInterceptor`() {
        assertNotNull(interceptor)
    }

    @Test
    fun `deve injetar OkHttpClient`() {
        assertNotNull(client)
    }

    @Test
    fun `deve injetar PokeApiService`() {
        assertNotNull(service)
    }
}