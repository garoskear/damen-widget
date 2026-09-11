plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Sürüm her push'ta otomatik artar: commit sayısı (CI'da fetch-depth:0 gerekir).
val commitCount: Int = try {
    ProcessBuilder("git", "rev-list", "--count", "HEAD")
        .directory(rootDir)
        .redirectErrorStream(true)
        .start().inputStream.bufferedReader().readText().trim().toInt()
} catch (_: Exception) { 1 }

android {
    namespace = "com.damen.widget"
    compileSdk = 34

    // Sabit imza: her CI derlemesi aynı anahtarla imzalanır, üstüne kurulum çalışır.
    signingConfigs {
        getByName("debug") {
            storeFile = file("damen-debug.keystore")
            storePassword = "android"
            keyAlias = "damen"
            keyPassword = "android"
        }
        create("release") {
            storeFile = file("damen-debug.keystore")
            storePassword = "android"
            keyAlias = "damen"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "com.damen.widget"
        minSdk = 29
        targetSdk = 34
        versionCode = commitCount
        versionName = "1.0.$commitCount"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
        aidl = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")

    // Jetpack Compose
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Glance widget
    implementation("androidx.glance:glance-appwidget:1.1.1")
    implementation("androidx.glance:glance-material3:1.1.1")

    // Shizuku: server durum kontrolü (durdur/başlat broadcast'leri saf intent, izin istemez)
    implementation("dev.rikka.shizuku:api:13.1.5")
    implementation("dev.rikka.shizuku:provider:13.1.5")

    // Periyodik widget yenileme
    implementation("androidx.work:work-runtime-ktx:2.9.0")
}
