plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

rootProject.name = "advent-of-code"
include("advents:aoc2021", "advents:aoc2022", "advents:aoc2023", "common:commons")
