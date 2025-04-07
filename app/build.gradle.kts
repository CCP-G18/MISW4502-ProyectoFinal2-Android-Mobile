plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.serialization)
    id("jacoco")
}

android {
    namespace = "com.g18.ccp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.g18.ccp"
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

    kotlin {
        sourceSets.all {
            languageSettings.optIn("kotlinx.serialization.ExperimentalSerializationApi")
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
        "**/*Test*",
        "**/*Activity*",
        "**/*Fragment*",
        "**/*Screen*",
        "**/*Composable*",
        "**/*Theme*",
        "**/*Type*",
        "**/*Color*",
        "**/*RetrofitProvider*",
        "**/com/g18/ccp/di*",
        "**/com/g18/ccp/data/remote/model*",
        "**/*LoginUiState*",
        "**/com/g18/ccp/core/utils/network/Output*",
        "**/*AuthService*",
        "**/*AuthenticationManager*",
        "**/com/g18/ccp/core/constants*",
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
        files(
            layout.buildDirectory.file("jacoco/testDebugUnitTest.exec").get().asFile,
            layout.buildDirectory.file(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"
            ).get().asFile
        ).filter { it.exists() }
    )
}

tasks.register("testDebugUnitTestCoverage") {
    dependsOn("testDebugUnitTest", "jacocoTestReport")
}


tasks.register("checkCoverage") {
    dependsOn("jacocoTestReport")

    doLast {
        val reportFile =
            layout.buildDirectory.file("reports/jacoco/jacocoTestReport/jacocoTestReport.xml").get()
        val htmlReportDir = layout.buildDirectory.dir("reports/jacoco/jacocoTestReport/html").get()
        val htmlReportLink = "file://${htmlReportDir.asFile.absolutePath}/index.html"

        if (!reportFile.asFile.exists()) {
            throw GradleException("❌ Coverage report not found. Make sure tests run successfully.")
        }

        val xmlContent = reportFile.asFile.readText()

        val classRegex =
            """<class name="([^"]+)".*?>.*?<counter type="INSTRUCTION" missed="(\d+)" covered="(\d+)"/>""".toRegex(
                RegexOption.DOT_MATCHES_ALL
            )
        val failingClasses = mutableListOf<Pair<String, Double>>()

        var globalMissed = 0
        var globalCovered = 0

        for (match in classRegex.findAll(xmlContent)) {
            val className = match.groupValues[1].replace("/", ".")
            val missed = match.groupValues[2].toInt()
            val covered = match.groupValues[3].toInt()
            val total = missed + covered
            val coverage = if (total > 0) covered.toDouble() / total * 100 else 100.0

            globalMissed += missed
            globalCovered += covered

            if (coverage < 70.0) {
                failingClasses.add(className to coverage)
            }
        }

        val globalCoverage = globalCovered.toDouble() / (globalCovered + globalMissed) * 100

        println("📊 Global Coverage: ${"%.2f".format(globalCoverage)}%")
        println("📄 HTML Report: $htmlReportLink")

        if (failingClasses.isNotEmpty()) {
            println("❌ Classes below 70% coverage:")
            failingClasses.sortedBy { it.second }.forEach { (name, cov) ->
                println(" - $name => ${"%.2f".format(cov)}%")
            }
            throw GradleException("❌ Some classes do not meet the coverage threshold.")
        } else {
            println("✅ All classes meet the 70% coverage threshold.")
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
    implementation(libs.androidx.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.security.crypto)
    implementation(libs.core.koin)
    implementation(libs.viewmodel.koin)
    implementation(libs.integration.koin)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.datastore)
    implementation(libs.serialization.json)
    implementation(libs.gson)
    implementation(libs.core.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.core)
    testImplementation(libs.robolectric)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
