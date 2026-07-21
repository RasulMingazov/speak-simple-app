plugins {
    alias(libs.plugins.speaksimple.android.application)
}

android {
    namespace = "org.speaksimpleapp"

    defaultConfig {
        applicationId = "org.speaksimpleapp"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${providers.gradleProperty("SPEAK_SIMPLE_API_BASE_URL").orElse("http://10.0.2.2:8080").get()}\""
        )
        buildConfigField(
            "String",
            "GOOGLE_SERVER_CLIENT_ID",
            "\"${providers.gradleProperty("SPEAK_SIMPLE_GOOGLE_SERVER_CLIENT_ID").orElse("").get()}\""
        )
    }

    buildFeatures.buildConfig = true
}

dependencies {
    implementation(project(":shared"))
    implementation(project(":feature-root"))
    implementation(project(":feature-auth"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.decompose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
