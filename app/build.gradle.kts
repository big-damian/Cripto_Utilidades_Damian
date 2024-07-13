plugins {
    id("com.android.application")
}

android {
    namespace = "com.damian.criptoutils"
    //compileSdk = 33

    //Cambio para corregir 'checkDebugAarMetadata'
    compileSdk = 34

    defaultConfig {
        applicationId = "com.damian.criptoutils"
        minSdk = 28
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment:2.5.3")
    implementation("androidx.navigation:navigation-ui:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.android.volley:volley:1.2.1") // Añadir volley para llamar a API
    //implementation("mysql:mysql-connector-java:8.0.20") // Añadir MySQL connector para conectar a MySQL
    //implementation("mysql:mysql-connector-java:8.0.26") // Añadir MySQL connector para conectar a MySQL
    implementation("mysql:mysql-connector-java:5.1.49") // Añadir MySQL connector para conectar a MySQL

    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Añadir Retrofit para cargar desde API
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Añadir Retrofit para parsear JSON desde APIs
    implementation("com.github.bumptech.glide:glide:4.12.0") // Añadir Picasso para pintar imagenes desde URL (Desde API)
}
