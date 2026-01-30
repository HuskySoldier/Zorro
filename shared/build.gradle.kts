plugins {
    // Estos nombres deben coincidir EXACTAMENTE con tu archivo libs.versions.toml
    alias(libs.plugins.kotlin.multiplatform) // Antes decía solo .multiplatform
    alias(libs.plugins.jetbrains.compose)    // Antes decía solo .compose
    alias(libs.plugins.kotlin.compose)       // Nuevo plugin para Kotlin 2.0
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.android.application)
}

kotlin {
    jvmToolchain(17)

    androidTarget() // Simplificado para tu app

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.multiplatformSettings)
            implementation(compose.material)
        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }
    }
}

android {
    // El namespace debe coincidir con el paquete de tu MainActivity
    namespace = "com.tuapp.zorro.sample.app"

    // Esta es la línea que falta:
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tuapp.zorro.sample.app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

// Esto es lo único que necesitas fuera de los bloques anteriores
room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("kspCommonMainMetadata", libs.androidx.room.compiler)
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}}