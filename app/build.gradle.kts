plugins {
<<<<<<< HEAD
=======
<<<<<<< HEAD
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.composeCompiler)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.fitnesstracker"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.fitnesstracker"
        minSdk = 26
        targetSdk = 36
=======
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.riseup_fitnesstracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.riseup_fitnesstracker"
        minSdk = 26
        targetSdk = 34
<<<<<<< HEAD
=======
>>>>>>> c578e340b6c58b6391e07561db14610aea4ef8b2
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
<<<<<<< HEAD
        vectorDrawables {
            useSupportLibrary = true
        }
=======
<<<<<<< HEAD
=======
        vectorDrawables {
            useSupportLibrary = true
        }
>>>>>>> c578e340b6c58b6391e07561db14610aea4ef8b2
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
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
<<<<<<< HEAD
=======
<<<<<<< HEAD
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
=======
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
<<<<<<< HEAD
=======
>>>>>>> c578e340b6c58b6391e07561db14610aea4ef8b2
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
<<<<<<< HEAD
=======
<<<<<<< HEAD
        kotlinCompilerExtensionVersion = "1.5.1"
=======
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
<<<<<<< HEAD
=======
>>>>>>> c578e340b6c58b6391e07561db14610aea4ef8b2
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
    }
}

dependencies {
<<<<<<< HEAD
=======
<<<<<<< HEAD
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.compose.material.icons.extended)
=======
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
<<<<<<< HEAD
=======
>>>>>>> c578e340b6c58b6391e07561db14610aea4ef8b2
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a

    implementation(libs.androidx.core.splashscreen)
    implementation("com.google.android.gms:play-services-fitness:21.2.0")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
<<<<<<< HEAD
=======
>>>>>>> c578e340b6c58b6391e07561db14610aea4ef8b2
>>>>>>> 5fa740e8defd87918d4fd59418947f73d6008b1a
}
