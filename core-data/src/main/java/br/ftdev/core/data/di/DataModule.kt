package br.ftdev.core.data.di

import androidx.room.Room
import br.ftdev.core.data.local.db.PokemonDatabase
import br.ftdev.core.data.remote.api.PokeApiService
import br.ftdev.core.data.repository.PokemonRepositoryImpl
import br.ftdev.core.domain.repository.PokemonRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

private const val TIMEOUT_SECONDS = 30L

val dataModule = module {

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = false
            encodeDefaults = true
        }
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl(PokeApiService.BASE_URL)
            .client(get<OkHttpClient>())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
    }

    single {
        get<Retrofit>().create(PokeApiService::class.java)
    }


    single<PokemonRepository> {
        PokemonRepositoryImpl(get(), get())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            PokemonDatabase::class.java,
            "pokemon_database"
        ).build()
    }

    single { get<PokemonDatabase>().pokemonDao() }
    single { get<PokemonDatabase>().pokemonDetailsDao() }

    single<PokemonRepository> {
        PokemonRepositoryImpl(
            pokeApiService = get(),
            pokemonDao = get(),
            pokemonDetailsDao = get()
        )
    }
}