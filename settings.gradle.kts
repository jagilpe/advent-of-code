plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

val kotlinVersion: String by System.getProperties()

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("libs") {
            val assertjVersion: String by System.getProperties()
            val kotlinCoroutinesVersion: String by System.getProperties()
            val jacksonVersion: String by System.getProperties()

            library("kotlinx-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").version(kotlinCoroutinesVersion)
            library("jackson-module-kotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin").version(jacksonVersion)

            bundle("aoc-implementation", listOf("kotlinx-coroutines", "jackson-module-kotlin"))

            library("assertj-core", "org.assertj", "assertj-core").version(assertjVersion)
            bundle("aoc-test", listOf("assertj-core"))
        }
    }
}

rootProject.name = "advent-of-code"
include("advents:aoc2021", "advents:aoc2022", "advents:aoc2023")
