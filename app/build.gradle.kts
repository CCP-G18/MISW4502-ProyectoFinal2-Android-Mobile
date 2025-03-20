plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

android {
    namespace = "com.g18.cpp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.g18.cpp"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
             enableUnitTestCoverage = true
        }
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
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)  // Necesario para validación automática
        html.required.set(true)
    }

    sourceDirectories.setFrom(files("$projectDir/src/main/java"))
    classDirectories.setFrom(
        files(fileTree("${layout.buildDirectory}/intermediates/javac/debug") {
            exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.class", "**/Manifest.class")
        })
    )
    executionData.setFrom(files("${layout.buildDirectory}/jacoco/testDebugUnitTest.exec"))
}

tasks.register("checkCoverage") {
    dependsOn("jacocoTestReport")

    doLast {
        val reportFile = file("${layout.buildDirectory}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        if (!reportFile.exists()) {
            throw GradleException("Coverage report not found")
        }

        val coverage = reportFile.readText()
            .substringAfter("<counter type=\"INSTRUCTION\" missed=\"")
            .substringBefore("\" covered=\"")
            .split("\" covered=\"")
            .map { it.toInt() }
            .let { (missed, covered) -> covered.toDouble() / (missed + covered) * 100 }

        println("Coverage: $coverage%")

        if (coverage < 70) {
            throw GradleException("Coverage is below 70%: $coverage%")
        }
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
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
