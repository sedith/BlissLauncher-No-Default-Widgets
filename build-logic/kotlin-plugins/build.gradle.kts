@file:Suppress("UnstableApiUsage", "DSL_SCOPE_VIOLATION")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins { `kotlin-dsl` }

dependencies { implementation(libs.build.spotless) }

afterEvaluate {
    tasks.withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_1_8.toString()
        targetCompatibility = JavaVersion.VERSION_1_8.toString()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
    }
}

gradlePlugin {
    plugins {
        register("spotless") {
            id = "foundation.e.blisslauncher.spotless"
            implementationClass = "foundation.e.blisslauncher.gradle.SpotlessPlugin"
        }
    }
}
