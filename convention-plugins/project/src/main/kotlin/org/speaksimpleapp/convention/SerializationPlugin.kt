package org.speaksimpleapp.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.speaksimpleapp.convention.extensions.commonMainDependencies
import org.speaksimpleapp.convention.extensions.libs

class SerializationPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.plugins.kotlin.serialization.get().pluginId)

        commonMainDependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
