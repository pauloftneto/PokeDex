package br.ftdev.core.ui.di

import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.io.File

val imageModule = module {
    single {
        ImageLoader.Builder(androidContext())
            .logger(DebugLogger())
            .memoryCache {
                MemoryCache.Builder(androidContext())
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(File(androidContext().cacheDir, "images"))
                    .maxSizeBytes(20L * 1024 * 1024)
                    .build()
            }
    }
}