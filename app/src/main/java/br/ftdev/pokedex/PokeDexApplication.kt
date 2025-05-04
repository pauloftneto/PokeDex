package br.ftdev.pokedex

import android.app.Application
import br.ftdev.core.data.di.dataModule
import br.ftdev.core.domain.di.domainModule
import br.ftdev.core.ui.di.imageModule
import br.ftdev.feature.pokedex.details.di.pokemonDetailsModule
import br.ftdev.feature.pokedex.di.featurePokeDexModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PokeDexApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@PokeDexApplication)
            modules(
                dataModule,
                imageModule,
                domainModule,
                featurePokeDexModule,
                pokemonDetailsModule
            )
        }
    }
}