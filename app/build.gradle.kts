import org.gradle.api.tasks.javadoc.Javadoc
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.google.common.collect.FluentIterable.from
import org.gradle.external.javadoc.StandardJavadocDocletOptions

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

val androidJavadocs by tasks.registering(Javadoc::class) {
    isFailOnError = false

    // Convert the FileCollection to a FileTree using asFileTree
    source = files(
        android.sourceSets["main"].java.srcDirs,
        "$buildDir/generated/source/buildConfig/debug",
        "$buildDir/generated/not_namespaced_r_class_sources/debug/r",
        "$buildDir/generated/source/dataBinding/java/main",
        "$buildDir/generated/source/kaptKotlin/debug"
    ).asFileTree


    // Include all compile-time dependencies
    classpath += files(android.bootClasspath.joinToString(File.pathSeparator))
    classpath += configurations["debugCompileClasspath"]

    // Exclude unwanted packages from the Javadoc output
    exclude("**/androidx/**", "**/com/google/**", "**/android/**")

    // Set the output directory for the generated Javadocs
    setDestinationDir(file("$rootDir/doc/javadoc"))
}



dependencies {
//    implementation(files("C:/Users/dcui7/AppData/Local/Android/Sdk/platforms/android-34/android.jar"))

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