import org.speaksimpleapp.convention.extensions.commonMainDependencies

plugins {
    alias(libs.plugins.speaksimple.kmp.library)
    alias(libs.plugins.speaksimple.compose.multiplatform)
    alias(libs.plugins.speaksimple.decompose)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

commonMainDependencies {
    implementation(project(":core-design"))
    implementation(project(":feature-root"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
}
