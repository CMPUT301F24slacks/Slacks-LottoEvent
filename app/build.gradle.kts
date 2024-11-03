import org.gradle.api.tasks.javadoc.Javadoc
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

androidComponents {
    onVariants { variant ->
        tasks.register("generate${variant.name.capitalize()}Javadoc", Javadoc::class) {
            description = "Generate ${variant.name} Javadoc"

            val javaCompileTask = tasks.named("compile${variant.name.capitalize()}JavaWithJavac", JavaCompile::class.java)
            source = javaCompileTask.get().source
            setDestinationDir(file("$rootDir/doc/javadoc"))

            isFailOnError = false

            doFirst {
                val androidJar = "${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"

                classpath = files(variant.compileClasspath) + files(androidJar)
                (options as StandardJavadocDocletOptions).addStringOption("-show-members", "package")
            }
        }
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
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("androidx.multidex:multidex:2.0.1")

    

}