plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android") version "1.8.10"
}

android {
    namespace = "com.example.brandtests"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.brandtests"
        minSdk = 31
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Bật DataBinding
    buildFeatures {
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0") // Cập nhật lên phiên bản mới nhất
    implementation("androidx.activity:activity:1.9.1") // Cập nhật lên phiên bản mới nhất
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.8.4") // Cập nhật lên phiên bản mới nhất
    implementation("androidx.lifecycle:lifecycle-livedata:2.8.4") // Cập nhật lên phiên bản mới nhất

    // Retrofit dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // RecyclerView dependencies
    implementation("androidx.recyclerview:recyclerview:1.2.1")

    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")

    // Lombok dependencies
    implementation("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")

    // Picasso dependency for image loading
    implementation("com.squareup.picasso:picasso:2.71828")

    // OkHttp Logging Interceptor for Retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
