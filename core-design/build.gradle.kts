plugins {
    alias(libs.plugins.speaksimple.kmp.library)
    alias(libs.plugins.speaksimple.compose.multiplatform)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.core.design"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}
