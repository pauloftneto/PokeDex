package br.ftdev.pokedex

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class PokemonApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin - Level.INFO ou Level.DEBUG são úteis durante o desenvolvimento
            androidLogger(Level.DEBUG)
            // Fornece o contexto do Android para o Koin
            androidContext(this@PokemonApplication)
            // Carrega os módulos de injeção de dependência
            modules(
                dataModule, // Carrega o módulo do core_data
                // Adicione outros módulos aqui (ex: domainModule, pokedexFeatureModule)
            )
        }
    }
}