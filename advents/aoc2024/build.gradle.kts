import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.jvm)
    alias(libs.plugins.compose)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(libs.bundles.aoc.implementation)
    implementation(project(":common:commons"))
    implementation(libs.kotlinx.coroutines)
    implementation(compose.desktop.currentOs)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.foundation)
    implementation(compose.runtime)

    testImplementation(kotlin("test"))
    testImplementation(libs.bundles.aoc.test)
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
    testLogging.showStandardStreams = true
    minHeapSize = "2048m"
    maxHeapSize = "6144m"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
}

// application {
//    mainClass.set("MainKt")
// }

compose.desktop {
    application {
        mainClass = "com.gilpereda.aoc2024.day14.VisualizationKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "aoc-visualizations"
            packageVersion = "1.0.0"
        }
    }
}
