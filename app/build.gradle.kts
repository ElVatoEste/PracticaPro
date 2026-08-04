    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.android)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.kotlinx.serialization)
        id("org.jetbrains.kotlin.kapt")

    }

    android {
        namespace = "com.vatodev.practicapro"
        compileSdk = 35

        defaultConfig {
            applicationId = "com.vatodev.practicapro"
            minSdk = 30
            targetSdk = 35
            versionCode = 10
            versionName = "2.1.0"
            buildConfigField("String", "DEVELOPER_NAME", "\"Vato_dev\"")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.getByName("debug")
            }
        }

        buildFeatures {
            buildConfig = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }

        kotlinOptions {
            freeCompilerArgs += listOf("-Xincremental")
            jvmTarget = "11"
        }
        buildFeatures {
            compose = true
        }
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.navigation.runtime.ktx)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.media3.exoplayer)
        implementation(libs.androidx.media3.ui)

        //Material desing
        implementation(libs.androidx.material3)
        implementation(libs.androidx.material.icons.core)
        implementation(libs.androidx.material.icons.extended)

        //Retrofit
        implementation(libs.retrofit)
        implementation(libs.converter.gson)
        implementation(libs.logging.interceptor)

        // Room dependencies
        implementation(libs.androidx.room.common)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.lifecycle.process)
        kapt(libs.androidx.room.compiler)

        // Kotlinx Serialization
        implementation(libs.kotlinx.serialization.json)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }