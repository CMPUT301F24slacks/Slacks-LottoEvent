import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}
secrets {
    defaultPropertiesFileName = "local.default.properties"
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
        // https://stackoverflow.com/a/78092051

        val properties = Properties()
        properties.load(rootProject.file("local.properties").inputStream())
        val mapsApiKey: String = properties.getProperty("MAPS_API_KEY") ?: ""
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        buildConfigField("String", "MAPS_API_KEY", properties.getProperty("MAPS_API_KEY"))

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
        buildConfig = true
    }
}

androidComponents {
    onVariants { variant ->
        tasks.register("generate${variant.name.capitalize()}Javadoc", Javadoc::class) {
            description = "Generate ${variant.name} Javadoc"

            val javaCompileTask = tasks.named(
                "compile${variant.name.capitalize()}JavaWithJavac",
                JavaCompile::class.java
            )
            source = javaCompileTask.get().source
            setDestinationDir(file("$rootDir/doc/javadoc"))

            isFailOnError = false

            doFirst {
                val androidJar =
                    "${android.sdkDirectory}/platforms/${android.compileSdkVersion}/android.jar"

                classpath = files(variant.compileClasspath) + files(androidJar)
                (options as StandardJavadocDocletOptions).addStringOption(
                    "-show-members",
                    "package"
                )
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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
    implementation(libs.fragment.testing)
    implementation(libs.activity)
    implementation(libs.firebase.storage)
    implementation(libs.ext.junit)
    implementation(libs.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.ext.junit)
    implementation(libs.espresso.core)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.0.1")
    testImplementation(libs.test.core)
    testImplementation(libs.androidx.core)
    androidTestImplementation(libs.junit.junit)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.0.1")
    testImplementation("org.apiguardian:apiguardian-api:1.1.0")
    testImplementation("org.robolectric:robolectric:4.8")
    testImplementation("org.robolectric:robolectric:4.10")
    testImplementation("org.hamcrest:hamcrest-library:2.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation("androidx.test:rules:1.5.0")
//    androidTestImplementation("androidx.test:core:1.5.0")
//    androidTestImplementation ("androidx.test:runner:1.5.2")
    implementation(libs.preference)
    testImplementation(libs.junit)
//    androidTestImplementation(libs.ext.junit)
//    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation ("com.google.firebase:firebase-storage:20.2.1")
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
    implementation("com.google.android.libraries.places:places:4.1.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
    testImplementation ("androidx.arch.core:core-testing:2.1.0")
    testImplementation ("org.mockito:mockito-core:4.8.1")
    testImplementation ("junit:junit:4.13.2")
    implementation ("androidx.test:core:1.4.1-alpha04")
    testImplementation ("androidx.test:monitor:1.4.0")
    debugImplementation ("androidx.test:monitor:1.6.0")


}