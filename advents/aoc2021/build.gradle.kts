plugins {
    kotlin("jvm") version "1.6.0"
}

group = "com.gilpereda.advents-of-code"
version = "1.0-SNAPSHOT"

val junitVersion by extra("5.7.0")

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.0.1"))

    implementation(kotlin("stdlib"))
    implementation("io.arrow-kt:arrow-core")


    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.assertj:assertj-core:3.18.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")

    testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    withType<Test> {
        useJUnitPlatform()
    }
}