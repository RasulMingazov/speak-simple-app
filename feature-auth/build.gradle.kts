import org.speaksimpleapp.convention.extensions.commonMainDependencies

plugins {
    alias(libs.plugins.speaksimple.kmp.library)
    alias(libs.plugins.speaksimple.compose.multiplatform)
    alias(libs.plugins.speaksimple.decompose)
    alias(libs.plugins.speaksimple.serialization)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.feature.auth"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
        withHostTest {}

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

commonMainDependencies {
    implementation(project(":core-common"))
    implementation(project(":core-design"))
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
}
