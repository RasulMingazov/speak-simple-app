import org.speaksimpleapp.convention.extensions.commonMainDependencies

plugins {
    alias(libs.plugins.speaksimple.kmp.library)
    alias(libs.plugins.speaksimple.compose.multiplatform)
    alias(libs.plugins.speaksimple.decompose)
    alias(libs.plugins.speaksimple.serialization)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.feature.main"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        withHostTest {}

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

commonMainDependencies {
    implementation(project(":feature-chat"))
}
