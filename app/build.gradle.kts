    import java.util.Properties

    plugins {
        alias(libs.plugins.android.application)
        alias(libs.plugins.kotlin.compose)
        alias(libs.plugins.kotlinx.serialization)
        alias(libs.plugins.ksp)
    }

    // Credenciales de firma. keystore.properties está en .gitignore; en CI se
    // usan las variables de entorno. Sin ninguna de las dos, la release sale
    // sin firmar en lugar de firmada con la clave de debug, que es pública.
    val keystoreProperties = Properties().apply {
        val file = rootProject.file("keystore.properties")
        if (file.exists()) file.inputStream().use { load(it) }
    }

    fun signingValue(key: String, env: String): String? =
        (keystoreProperties.getProperty(key) ?: System.getenv(env))?.takeIf { it.isNotBlank() }

    val storeFilePath = signingValue("storeFile", "PRACTICAPRO_STORE_FILE")
    val storePasswordValue = signingValue("storePassword", "PRACTICAPRO_STORE_PASSWORD")
    val keyAliasValue = signingValue("keyAlias", "PRACTICAPRO_KEY_ALIAS")
    val keyPasswordValue = signingValue("keyPassword", "PRACTICAPRO_KEY_PASSWORD")

    val releaseSigningReady =
        storeFilePath != null && storePasswordValue != null &&
            keyAliasValue != null && keyPasswordValue != null

    android {
        namespace = "com.vatodev.practicapro"
        compileSdk = 37

        defaultConfig {
            applicationId = "com.vatodev.practicapro"
            minSdk = 30
            targetSdk = 36
            versionCode = 11
            versionName = "2.2.0"
            buildConfigField("String", "DEVELOPER_NAME", "\"Vato_dev\"")
            buildConfigField("boolean", "BACKEND_ENABLED", "false")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        signingConfigs {
            if (releaseSigningReady) {
                create("release") {
                    storeFile = file(storeFilePath!!)
                    storePassword = storePasswordValue
                    keyAlias = keyAliasValue
                    keyPassword = keyPasswordValue
                }
            }
        }

        buildTypes {
            release {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
                signingConfig = signingConfigs.findByName("release")
            }
        }

        buildFeatures {
            buildConfig = true
            compose = true
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }

    // MigrationTestHelper lee los esquemas desde los assets de androidTest.
    // Sin esta línea no encuentra ninguno y todas las pruebas de migración
    // fallan al ejecutarse, aunque compilen.
    android.sourceSets.getByName("androidTest").assets.srcDir("$projectDir/schemas")

    kotlin {
        jvmToolchain(21)

        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
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
        implementation(libs.converter.kotlinx)
        implementation(platform(libs.okhttp.bom))
        implementation(libs.logging.interceptor)

        // Room dependencies
        implementation(libs.androidx.room.common)
        implementation(libs.androidx.room.ktx)
        implementation(libs.androidx.lifecycle.process)
        ksp(libs.androidx.room.compiler)

        // Kotlinx Serialization
        implementation(libs.kotlinx.serialization.json)

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.room.testing)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }