package org.speaksimpleapp.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.speaksimpleapp.convention.extensions.libs
import org.speaksimpleapp.convention.extensions.projectJavaVersion

class AndroidApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        pluginManager.apply(libs.plugins.android.application.get().pluginId)
        pluginManager.apply(libs.plugins.compose.multiplatform.get().pluginId)
        pluginManager.apply(libs.plugins.kotlin.compose.get().pluginId)

        extensions.configure<ApplicationExtension> {
            compileSdk = libs.versions.compileSdk.get().toInt()

            defaultConfig {
                minSdk = libs.versions.minSdk.get().toInt()
                targetSdk = libs.versions.targetSdk.get().toInt()
                versionCode = libs.versions.appVersionCode.get().toInt()
                versionName = libs.versions.appVersionName.get()
            }

            compileOptions {
                sourceCompatibility = projectJavaVersion
                targetCompatibility = projectJavaVersion
            }

            buildFeatures {
                compose = true
            }
        }

        gradle.projectsEvaluated {
            rootProject.subprojects
                .filter { it != this@with }
                .forEach { subproject ->
                    val copyComposeResourcesTasks = subproject.tasks.matching {
                        it.name.endsWith("ComposeResourcesToAndroidAssets")
                    }

                    if (!copyComposeResourcesTasks.isEmpty()) {
                        target.extensions.configure<ApplicationExtension> {
                            sourceSets.getByName("main").assets.srcDir(
                                subproject.layout.buildDirectory.dir(
                                    "generated/assets/copyAndroidMainComposeResourcesToAndroidAssets"
                                ).get().asFile
                            )
                        }

                        target.tasks.matching { it.name.endsWith("Assets") }.configureEach {
                            dependsOn(copyComposeResourcesTasks)
                        }
                    }
                }
        }
    }
}
