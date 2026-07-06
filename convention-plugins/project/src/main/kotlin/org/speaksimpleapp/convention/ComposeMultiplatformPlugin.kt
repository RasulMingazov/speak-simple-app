package org.speaksimpleapp.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.speaksimpleapp.convention.extensions.commonMainDependencies
import org.speaksimpleapp.convention.extensions.libs

class ComposeMultiplatformPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.plugins.compose.multiplatform.get().pluginId)
        pluginManager.apply(libs.plugins.kotlin.compose.get().pluginId)

        commonMainDependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.resources)
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
        }
    }
}
