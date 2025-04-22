plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("com.google.dagger.hilt.android") version "2.55" apply true
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    alias(libs.plugins.compose.compiler)
    //id("kotlinx-serialization")
    kotlin("plugin.serialization") version "2.0.0"

}

val hilt_version = "2.55"
android {
    namespace = "com.vhennus"
    compileSdk = 35

    flavorDimensions +="environment"
    productFlavors {

        create("prod") {
            dimension = "environment"
            // Additional configurations for this flavor

//            buildConfigField("String", "API_URL", "\"http://155.138.224.183:8000\"")

            //buildConfigField("String", "API_URL", "\"http://0.0.0.0:8000\"")
            buildConfigField("String", "API_URL", "\"https://bend.vhennus.com:7000\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.221.87:3000\"")
        }
        create("dev") {
            dimension = "environment"
            // Additional configurations for this flavor
            applicationIdSuffix = ".dev"
            //buildConfigField("String", "API_URL", "\"http://155.138.224.183:8000\"")
            //buildConfigField("String", "API_URL", "\"http://10.0.2.2:8000\"")
            buildConfigField("String", "API_URL", "\"https://bend.vhennus.com:8000\"")
            //buildConfigField("String", "API_URL", "\"http://172.20.10.2:8000\"")
            //buildConfigField("String", "API_URL", "\"http://0.0.0.0:8000\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.221.87:3000\"")

        }
    }
    defaultConfig {
        applicationId = "com.vhennus"
        minSdk = 28
        targetSdk = 34
        versionCode = 10
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        multiDexEnabled = true
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }
    splits {
        abi {
            isEnable = false
            isUniversalApk = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )


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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }




}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.play.services.cast.tv)
    implementation(libs.androidx.foundation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.metadata.jvm)

    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // material 3
    implementation(libs.androidx.material3.v121)
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.0-beta03")
    implementation("androidx.compose.material:material-icons-extended:1.6.8")

    // hilt
    implementation ("com.google.dagger:hilt-android:$hilt_version")
    kapt ("com.google.dagger:hilt-compiler:$hilt_version")
    implementation ("androidx.hilt:hilt-navigation-compose:1.2.0")
    kapt ("com.google.dagger:hilt-android-compiler:2.48")

    // coroutine
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")


    // gson
    implementation("com.google.code.gson:gson:2.11.0")

    //lotie animation

    implementation("com.airbnb.android:lottie-compose:4.0.0")

    // room db
    val room_version = "2.6.1"

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)

    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // To use Kotlin Symbol Processing (KSP)
    //ksp("androidx.room:room-compiler:$room_version")


    // retrofit
    implementation (libs.retrofit)
    implementation(libs.converter.gson)

    // crypto shared
    implementation ("androidx.security:security-crypto:1.1.0-alpha06")

    // sentry
    implementation (libs.sentry.android)

    // pretty date
    implementation (libs.prettytime)

    implementation ("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // image
    implementation(libs.coil.compose)

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:32.1.0"))
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation ("com.google.firebase:firebase-messaging:24.1.0")
    implementation ("com.google.firebase:firebase-analytics:22.2.0")
    // cloudinary
    implementation(libs.cloudinary.android)
    implementation("androidx.activity:activity-compose:1.7.2")

    implementation("androidx.work:work-runtime-ktx:2.8.0")

    implementation("com.facebook.soloader:soloader:0.12.1")


    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // kotlin serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")


    // system ui controller
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.31.0-alpha")

    // google font
    implementation ("androidx.compose.ui:ui-text-google-fonts:1.7.8")

    // splash screen
    implementation ("androidx.core:core-splashscreen:1.0.1")

    // link detection
    implementation("sh.calvin.autolinktext:autolinktext:2.0.1")

    // bouncy castle encryption
    implementation("org.bouncycastle:bcprov-jdk15to18:1.78")

}

kapt {
    correctErrorTypes = true
}

hilt {
    enableAggregatingTask = false
}
