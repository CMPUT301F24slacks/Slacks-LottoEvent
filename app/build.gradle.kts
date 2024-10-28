import com.android.build.gradle.LibraryExtension
import org.gradle.api.tasks.javadoc.Javadoc
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.external.javadoc.StandardJavadocDocletOptions

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("org.jetbrains.dokka") version "1.8.10"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

// Ensure the task is registered after Android configuration
afterEvaluate {
    tasks.register<Javadoc>("generateJavadoc") {
        description = "Generates Javadoc for Java files in the Android project."

        // Access the Java source directories for the main source set
        val mainJavaSrcDirs = android.sourceSets.getByName("main").java.srcDirs
        source = files(mainJavaSrcDirs).asFileTree

        // Set failOnError to false to prevent build failure on Javadoc errors
        isFailOnError = false

        doFirst {
            val androidJar = "${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"

            // Set the classpath to include Android boot classpath and runtime classpath
            classpath = files(
                android.bootClasspath, // Android boot classpath
                configurations.getByName("releaseRuntimeClasspath") // Runtime classpath for release build
            )
            (options as StandardJavadocDocletOptions).addStringOption("-show-members", "package")
        }

        // Exclude generated files and unnecessary resources
        exclude("**/R.java", "**/BuildConfig.java", "**/Manifest.java")

        // Set Javadoc options
        options.encoding = "UTF-8"

        // Suppress warnings for Java 11 and above
        if (JavaVersion.current().isJava11Compatible) {
            (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        }

        // Set the destination directory for the generated Javadoc
        setDestinationDir(file("$buildDir/docs/javadoc"))
    }
}

dependencies {
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.google.guava:guava:31.0.1-android")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")

    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.2.0")
    implementation("androidx.camera:camera-view:1.2.0")
    implementation(libs.zxing.android.embedded.v410)
    implementation(libs.core)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation("junit:junit:4.13.2")
}
