plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("jacoco")
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
            isMinifyEnabled = false
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
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
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

tasks.withType<Test> {
    finalizedBy("jacocoTestReport")
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    mustRunAfter("testDebugUnitTest")
    outputs.upToDateWhen { false }

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R\$*.class",
        "**/BuildConfig.class",
        "**/Manifest.class",
        "**/*Test*"
    )

    // 🔥 Updated paths for compiled classes (for both Java and Kotlin)
    val javaTree =
        layout.buildDirectory.dir("intermediates/javac/debug/classes").orNull?.asFileTree?.matching {
            exclude(fileFilter)
        } ?: fileTree(layout.buildDirectory.dir("tmp/kotlin-classes/debug")).matching {
            exclude(
                fileFilter
            )
        }

    val kotlinTree =
        layout.buildDirectory.dir("tmp/kotlin-classes/debug").orNull?.asFileTree?.matching {
            exclude(fileFilter)
        } ?: fileTree(layout.buildDirectory.dir("intermediates/javac/debug/classes")).matching {
            exclude(
                fileFilter
            )
        }

    sourceDirectories.setFrom(files("${project.projectDir}/src/main/java"))
    classDirectories.setFrom(files(javaTree, kotlinTree))

    // 🔥 Corrected execution data path
    executionData.setFrom(
        layout.buildDirectory.file(
            "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
        )
    )
}

tasks.register("testDebugUnitTestCoverage") {
    dependsOn("testDebugUnitTest", "jacocoTestReport")
}


tasks.register("checkCoverage") {
    dependsOn("jacocoTestReport")

    doLast {
        val reportFile = layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml").get()
        val htmlReportDir = layout.buildDirectory.dir("reports/jacoco/jacocoTestReport/html").get()
        val htmlReportLink = "file://${htmlReportDir.asFile.absolutePath}/index.html"

        if (!reportFile.asFile.exists()) {
            throw GradleException("❌ Coverage report not found. Make sure tests run successfully.")
        }

        val xmlContent = reportFile.asFile.readText()
        val regex =
            """<counter type="INSTRUCTION" missed="(\d+)" covered="(\d+)"/>""".toRegex()
        val matchResult = regex.find(xmlContent)

        if (matchResult != null) {
            val missed = matchResult.groupValues[1].toInt()
            val covered = matchResult.groupValues[2].toInt()
            val coverage = covered.toDouble() / (missed + covered) * 100

            println(" Code Coverage: $coverage%")
            println(" Coverage Report: $htmlReportLink")

            if (coverage < 70) {
                throw GradleException("❌ Code coverage is below 70%: $coverage%")
            } else {
                println("✅ Code coverage meets the threshold: $coverage%")
            }
        } else {
            throw GradleException("❌ No valid coverage data found in Jacoco report.")
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
