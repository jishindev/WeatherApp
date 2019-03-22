plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "dev.jishin.android.weatherapp"
        minSdkVersion(16)
        targetSdkVersion(28)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    sourceSets["main"].java.srcDir("src/main/kotlin")
}

dependencies {

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk7", version = "1.3.21"))

    // Core
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.0.1")
    implementation("com.android.support:multidex:1.0.3")

    // Design
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.google.android.material:material:1.0.0")

    // Lifecycle - ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-extensions:2.0.0")
    //kapt("androidx.lifecycle:compiler:2.0.0")

    // Dagger DI
    implementation("com.google.dagger:dagger:2.16")
    kapt("com.google.dagger:dagger-compiler:2.16")
    implementation("com.google.dagger:dagger-android:2.16")
    kapt("com.google.dagger:dagger-android-processor:2.16")

    // Location
    implementation("com.google.android.gms:play-services-location:16.0.0")

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")
    implementation("ru.gildor.coroutines:kotlin-coroutines-retrofit:1.1.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.8.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.5.0")

    // Logging
    implementation("com.jakewharton.timber:timber:4.7.1")

    // Permissions.
    implementation("com.github.fondesa:kpermissions:1.0.0")

    // Testing
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")

    // Mock
    testImplementation("org.mockito:mockito-core:2.25.0")
    androidTestImplementation("org.mockito:mockito-android:2.25.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.13.1")
}
