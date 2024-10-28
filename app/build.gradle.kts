import org.gradle.api.tasks.javadoc.Javadoc

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.slacks_lottoevent"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.slacks_lottoevent"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }

    tasks.register("generateReleaseJavadoc", Javadoc::class) {
        description = "Generates Javadoc for the release build."

        // Set the source files for Javadoc generation
        source = fileTree("src/main/java") {
            include("**/*.java")
        }

        // Specify the destination directory for the generated Javadoc
        setDestinationDir(file("$buildDir/docs/javadoc/release"))

        // Set the classpath to include the runtime classpath and the Android SDK
        doFirst {
            val androidJar = "${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"
            classpath = files(configurations["runtimeClasspath"]) + files(androidJar)
        }

        // Exclude generated files from Javadoc
        exclude("**/BuildConfig.java", "**/R.java")
    }


}

dependencies {
    implementation("com.google.guava:guava:31.0.1-android")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)
    implementation(libs.firebase.crashlytics.buildtools)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore")

    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.2.0")
    implementation("androidx.camera:camera-view:1.2.0")
    implementation(libs.zxing.android.embedded.v410)
    implementation(libs.core)

}