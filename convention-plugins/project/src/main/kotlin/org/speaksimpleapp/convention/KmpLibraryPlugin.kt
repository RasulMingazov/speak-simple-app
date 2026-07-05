package org.speaksimpleapp.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.speaksimpleapp.convention.extensions.commonMainDependencies
import org.speaksimpleapp.convention.extensions.commonTestDependencies
import org.speaksimpleapp.convention.extensions.kotlinMultiplatform
import org.speaksimpleapp.convention.extensions.libs

class KmpLibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.plugins.kotlin.multiplatform.get().pluginId)
        pluginManager.apply(libs.plugins.android.kotlin.multiplatform.library.get().pluginId)

        kotlinMultiplatform {
            listOf(
                iosArm64(),
                iosSimulatorArm64()
            ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                    baseName = project.name
                        .split("-")
                        .joinToString("") { part -> part.replaceFirstChar(Char::uppercaseChar) }
                    isStatic = true
                }
            }
        }

        commonMainDependencies {
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTestDependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
