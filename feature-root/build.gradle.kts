import org.speaksimpleapp.convention.extensions.commonMainDependencies

plugins {
    alias(libs.plugins.speaksimple.kmp.library)
    alias(libs.plugins.speaksimple.compose.multiplatform)
    alias(libs.plugins.speaksimple.decompose)
    alias(libs.plugins.speaksimple.serialization)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.feature.root"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

commonMainDependencies {
    implementation(project(":core-common"))
    implementation(project(":core-design"))
    implementation(project(":feature-auth"))
    implementation(project(":feature-chat"))
}
