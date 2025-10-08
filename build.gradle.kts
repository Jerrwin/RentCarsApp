plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}

buildscript {
    dependencies {
        // Add the classpath for Google services plugin
        classpath(libs.google.services)
    }
}

allprojects {
    // No need to specify repositories here
}
