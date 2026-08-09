plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.kian.mahmoudi.vegang"
    compileSdk {
        version = release(35)
    }

    defaultConfig {
        applicationId = "com.kian.mahmoudi.vegang"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
    }
}

dependencies {
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.appcompat)
    implementation(project(":v2ray"))
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("com.github.GrenderG:Toasty:1.5.2")
    implementation("com.squareup.okhttp3:okhttp:5.2.0")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("com.squareup.okhttp3:logging-interceptor:5.2.0")
    implementation("androidx.room:room-ktx:2.8.4")
    implementation("com.google.dagger:hilt-android:2.57.1")
    ksp("androidx.room:room-compiler:2.8.4")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel-compose:1.3.0")
    ksp("com.google.dagger:hilt-android-compiler:2.57.1")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}