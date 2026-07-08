plugins {
    alias(libs.plugins.speaksimple.kmp.library)
}

kotlin {
    android {
        namespace = "org.speaksimpleapp.core.common"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.essenty.instance.keeper)
        }
    }
}
