
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.mist.launcher"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mist.launcher"
        minSdk = 23
        targetSdk = 35

        versionCode = providers
            .environmentVariable("MIST_VERSION_CODE")
            .orElse("2")
            .get()
            .toInt()

        versionName = providers
            .environmentVariable("MIST_VERSION_NAME")
            .orElse("1.2.0")
            .get()
    }

    signingConfigs {
        create("release") {
            storeFile = file(
                System.getenv("MIST_KEYSTORE_PATH") ?: "mist-release.jks"
            )
            storePassword = System.getenv("MIST_KEYSTORE_PASSWORD") ?: ""
            keyAlias = System.getenv("MIST_KEY_ALIAS") ?: "mist"
            keyPassword = System.getenv("MIST_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))

    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.core:core-ktx:1.15.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    debugImplementation("androidx.compose.ui:ui-tooling")
}


    
