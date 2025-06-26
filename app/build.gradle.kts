
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.safeargs)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.payflowapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.payflowapp"
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
}

dependencies {
    implementation(libs.androidx.gridlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    val nav_version = "2.9.0"
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

   // alias(libs.plugins.safeargs)

    // Firebase BOM (debe ir antes de las demás dependencias de Firebase)
    implementation(platform("com.google.firebase:firebase-bom:25.11.0"))

    // Firebase Auth y Firestore
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // Firebase Kotlin Extensions (si los necesitas)
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // SMS Retriever API (opcional pero útil para auto-captura del OTP)
    implementation("com.google.android.gms:play-services-auth-api-phone:17.5.1")

    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.libraries.places:places:3.3.0")

}