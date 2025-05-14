plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.lespetitespuces"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.lespetitespuces"
        minSdk = 24
        targetSdk = 35
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
    composeOptions{
        kotlinCompilerExtensionVersion="1.5.1"
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // Firebase (using BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.3.1")) // Updated to latest stable version
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-database-ktx") // Use ktx version

    // Other libraries
    implementation("io.coil-kt:coil-compose:2.4.0") // Updated version
    implementation("com.google.accompanist:accompanist-pager:0.30.1") // Updated version
    implementation("com.google.accompanist:accompanist-pager-indicators:0.30.1") // Updated version

    // Remove duplicate or conflicting dependencies
    // implementation(libs.firebase.database) // Remove this line if it's not the ktx version
    // implementation(libs.androidx.appcompat) // Not typically needed with Compose
    // implementation(libs.androidx.activity) // Already covered by activity-compose
    // implementation(libs.androidx.constraintlayout) // Use constraintlayout-compose instead

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.appcompat:appcompat:1.6.1")  // Explicit version
}