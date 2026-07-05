plugins {
    alias(libs.plugins.speaksimple.android.application)
}

android {
    namespace = "org.speaksimpleapp"

    defaultConfig {
        applicationId = "org.speaksimpleapp"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":feature-root"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.decompose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
