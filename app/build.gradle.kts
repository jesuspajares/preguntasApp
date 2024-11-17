plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.tuempresa.preguntasapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tuempresa.preguntasapp"
        minSdk = 26
        targetSdk = 34
        compileSdk = 34
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
    buildFeatures {
        compose = true
    }

    // Agregar tarea para desinstalar la aplicación antes de construir
    applicationVariants.all {
        preBuildProvider.configure {
            doFirst {
                exec {
                    commandLine("adb", "uninstall", "com.tuempresa.preguntasapp")
                }
            }
        }
    }
}

dependencies {
    // Dependencias de implementación
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.apache.poi:poi:5.2.3")
    implementation("org.apache.poi:poi-ooxml:5.2.3")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Dependencias para pruebas unitarias
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("org.mockito:mockito-inline:3.11.2")
    testImplementation("androidx.test:core:1.4.0")
    testImplementation("androidx.test.ext:junit:1.1.3")
    testImplementation("org.hamcrest:hamcrest-library:2.2")

    // Dependencias para pruebas de integración (Android Instrumentation Tests)
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Dependencias de depuración
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
