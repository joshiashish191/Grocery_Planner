plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "net.softglobe.groceryplanner"
    compileSdk = 34

    android {
        buildFeatures {
            buildConfig = true
        }
    }

    defaultConfig {
        applicationId = "net.softglobe.groceryplanner"
        minSdk = 24
        targetSdk = 33
        versionCode = 4
        versionName = "1.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://groceryplanner.softglobe.net/api/\"")
            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            isDebuggable = true
            buildConfigField("String", "BASE_URL", "\"http://192.168.0.104/groceryplanner/api/\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    dataBinding {
        enable = true
    }
}


dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.1")
    implementation("com.google.firebase:firebase-crashlytics:18.6.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Room
    val roomVersion = "2.6.0-alpha01"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    // To use Kotlin annotation processing tool (kapt)
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    //Coroutine
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //Splash Screen
    implementation("androidx.core:core-splashscreen:1.1.0-alpha02")
    //Stripe
    implementation("com.stripe:stripe-android:20.34.4")
    //Lottie Loading Animation
    implementation("com.airbnb.android:lottie:6.2.0")
    //Admob
    implementation("com.google.android.gms:play-services-ads:22.5.0")
}