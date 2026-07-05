package org.speaksimpleapp.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.speaksimpleapp.convention.extensions.commonMainDependencies
import org.speaksimpleapp.convention.extensions.libs

class DecomposePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        commonMainDependencies {
            implementation(libs.decompose)
            implementation(libs.decompose.extensions.compose)
        }
    }
}
