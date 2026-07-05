import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "org.speaksimpleapp.convention"

dependencies {
    implementation(libs.gradle.plugin.android.tools)
    implementation(libs.gradle.plugin.compose)
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.kotlin.compose)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

private val projectJavaVersion = JavaVersion.toVersion(libs.versions.java.get())

java {
    sourceCompatibility = projectJavaVersion
    targetCompatibility = projectJavaVersion
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(projectJavaVersion.toString()))
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "org.speaksimpleapp.android.application"
            implementationClass = "org.speaksimpleapp.convention.AndroidApplicationPlugin"
        }
        register("kmpLibrary") {
            id = "org.speaksimpleapp.kmp.library"
            implementationClass = "org.speaksimpleapp.convention.KmpLibraryPlugin"
        }
        register("composeMultiplatform") {
            id = "org.speaksimpleapp.compose.multiplatform"
            implementationClass = "org.speaksimpleapp.convention.ComposeMultiplatformPlugin"
        }
        register("decompose") {
            id = "org.speaksimpleapp.decompose"
            implementationClass = "org.speaksimpleapp.convention.DecomposePlugin"
        }
        register("serialization") {
            id = "org.speaksimpleapp.serialization"
            implementationClass = "org.speaksimpleapp.convention.SerializationPlugin"
        }
    }
}
