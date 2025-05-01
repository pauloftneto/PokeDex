plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "br.ftdev.core_domain"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(project(":core-data"))

    // Kotlin & Coroutines
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Dependency Injection (Koin)
    // Koin Core features
    implementation(libs.koin.core)
    // Koin Android features
    implementation(libs.koin.android)
    // Koin for Jetpack Compose (se usar Compose futuramente)
    // implementation("io.insert-koin:koin-androidx-compose:3.5.3")

    // Koin Test features
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
}