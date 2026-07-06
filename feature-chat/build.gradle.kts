import org.speaksimpleapp.convention.extensions.commonMainDependencies

plugins {
    alias(libs.plugins.speaksimple.kmp.library)
    alias(libs.plugins.speaksimple.compose.multiplatform)
    alias(libs.plugins.speaksimple.decompose)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.feature.chat"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        withHostTest {}

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

commonMainDependencies {
    implementation(project(":core-common"))
}
