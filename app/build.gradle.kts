plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.vhennus"
    compileSdk = 35

    flavorDimensions +="environment"
    productFlavors {

        create("prod") {
            dimension = "environment"
            // Additional configurations for this flavor

            buildConfigField("String", "API_URL", "\"https://bend.vhennus.com\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.221.87:9990\"")
            buildConfigField("String", "WEB_SOCKET_API_URL", "\"wss://bend.vhennus.com\"")
        }
        create("dev") {
            dimension = "environment"
            // Additional configurations for this flavor
            applicationIdSuffix = ".dev"
            buildConfigField("String", "API_URL", "\"https://testbend.vhennus.com\"")
            buildConfigField("String", "BLOCKCHAIN_URL", "\"155.138.221.87:3000\"")
            buildConfigField("String", "WEB_SOCKET_API_URL", "\"wss://testbend.vhennus.com\"")

        }
    }
    defaultConfig {
        applicationId = "com.vhennus"
        minSdk = 27
        targetSdk = 34
        versionCode = 11
        versionName = "1.1"

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
            signingConfig = signingConfigs.getByName("debug")
//           isShrinkResources = true
//            isDebuggable =true
//            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isMinifyEnabled = true
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
    implementation (libs.firebase.messaging)
    implementation (libs.firebase.analytics)

    // cloudinary
    implementation(libs.cloudinary.android)
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.soloader)

    // kotlin serialization
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit2.kotlinx.serialization.converter)


    // system ui controller
    implementation (libs.accompanist.systemuicontroller)

    // google font
    implementation (libs.androidx.ui.text.google.fonts)

    // splash screen
    implementation (libs.androidx.core.splashscreen)

    // link detection
    implementation(libs.autolinktext)

    // bouncy castle encryption
    implementation(libs.bcprov.jdk15to18)

    // dns
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:4.12.0")
    //implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))

}

hilt {
    enableAggregatingTask = false
}
