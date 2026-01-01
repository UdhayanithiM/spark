plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.example.sparkapp"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.sparkapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- THIS IS THE NEW LINE TO FIX THE ICON ERRORS ---
    implementation("androidx.compose.material:material-icons-extended-android:1.6.8")
    implementation("androidx.compose.material:material-icons-extended:1.6.6")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Networking (Retrofit for API calls, Moshi for JSON)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

    // --- THIS IS THE FIX (using the versions from your snippet) ---
    implementation("com.squareup.moshi:moshi:1.15.0") // <-- THIS LINE WAS MISSING
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0") // <-- This line was "1.14.0", now updated

    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0") // You can also update this to 1.15.0
    implementation("com.google.code.gson:gson:2.10.1")
    // For Swipe-to-Refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.31.0-alpha")

    // --- UPDATED DEPENDENCIES (from your file) ---
    // Video Player (Media3/ExoPlayer) - Updated to new versions
    implementation("androidx.media3:media3-exoplayer:1.3.1") // Was 1.2.0
    implementation("androidx.media3:media3-ui:1.3.1")       // Was 1.2.0

    // Needed to host the Android PlayerView inside a Composable
    implementation("androidx.compose.ui:ui-viewbinding:1.6.7")
    // --- END OF UPDATES ---

    // ViewModel & Navigation
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // --- THIS IS THE NEW LINE YOU NEED TO ADD ---
    implementation(libs.androidx.datastore.preferences)

}