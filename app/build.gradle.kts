plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.catignascabela.dodapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.catignascabela.dodapplication"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        viewBinding { enable = true }
        buildFeatures {
            dataBinding = true // Keep this if you're using data binding
        }
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
        sourceCompatibility = JavaVersion.VERSION_17 // Align with toolchain
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.github.TutorialsAndroid:GButton:v1.0.19")
    implementation(libs.play.services.auth)
    implementation(libs.appcompat) // Ensure no duplicate entries
    implementation(libs.fragment)
    implementation(libs.circleimageview)
    implementation(libs.lifecycle.extensions)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)

    // Use Firebase BOM to manage versions automatically
    implementation(platform("com.google.firebase:firebase-bom:33.5.1"))

    // Firebase dependencies (versions managed by BOM)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17) // Align toolchain with Java version
    }
}
apply(plugin = "com.google.gms.google-services")