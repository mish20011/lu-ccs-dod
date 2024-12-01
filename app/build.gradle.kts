plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.catignascabela.dodapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.catignascabela.dodapplication"
        minSdk = 30
        targetSdk = 35
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
    implementation(libs.play.services.base)
    implementation(libs.appcompat) // Ensure no duplicate entries
    implementation(libs.fragment)
    implementation(libs.circleimageview)
    implementation(libs.lifecycle.extensions)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.cardview)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation("com.google.android.material:material:1.12.0")
    implementation("com.google.firebase:firebase-installations:18.0.0")
    implementation(libs.firebase.database)
    implementation ("androidx.navigation:navigation-fragment:2.8.4")
    implementation ("androidx.navigation:navigation-ui:2.8.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17) // Align toolchain with Java version
    }
}
