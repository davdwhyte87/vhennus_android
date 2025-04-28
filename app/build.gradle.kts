plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    kotlin("plugin.serialization") version "2.0.0"

}

android {
    namespace = "com.vhennus"
    compileSdk = 35

    flavorDimensions +="environment"
    productFlavors {

        create("prod") {
            dimension = "environment"
            // Additional configurations for this flavor

//            buildConfigField("String", "API_URL", "\"http://155.138.224.183:8000\"")

//            buildConfigField("String", "API_URL", "\"http://0.0.0.0:8000\"")
            buildConfigField("String", "API_URL", "\"http://172.20.10.2:8000\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.224.183:3000\"")
        }
        create("dev") {
            dimension = "environment"
            // Additional configurations for this flavor
            applicationIdSuffix = ".dev"
            //buildConfigField("String", "API_URL", "\"http://155.138.224.183:8000\"")
//            buildConfigField("String", "API_URL", "\"http://10.0.2.2:8000\"")
            buildConfigField("String", "API_URL", "\"http://155.138.221.87:8000\"")
//            buildConfigField("String", "API_URL", "\"http://172.20.10.2:8000\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.221.87:3000\"")

        }
    }
    defaultConfig {
        applicationId = "com.vhennus"
        minSdk = 28
        targetSdk = 34
        versionCode = 9
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
//           isShrinkResources = true
//            isDebuggable =true
//            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
        viewBinding = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.androidx.room.compiler)
    // Lottie
    implementation(libs.lottie.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.play.services.cast.tv)

    // crypto shared
    implementation (libs.androidx.security.crypto)

    // sentry
    implementation (libs.sentry.android)

    // pretty date
    implementation (libs.prettytime)

    // image
    implementation(libs.coil.compose)

    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage.ktx)
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.google.firebase:firebase-analytics:22.2.0")

    // cloudinary
    implementation(libs.cloudinary.android)
    implementation(libs.androidx.work.runtime.ktx)

    implementation("com.facebook.soloader:soloader:0.12.1")

    // kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")


}
