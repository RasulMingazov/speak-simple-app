pluginManagement {
    includeBuild("convention-plugins/project")

    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "speak-simple-app"
include(":app")
include(":core-common")
include(":core-test")
include(":core-design")
include(":shared")
include(":feature-root")
include(":feature-main")
include(":feature-chat")
include(":feature-auth")
