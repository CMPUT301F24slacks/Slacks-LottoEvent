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
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("androidx.multidex:multidex:2.0.1")

}

