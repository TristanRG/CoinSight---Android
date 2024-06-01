// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        val kotlin_version = "1.9.0"
        val nav_version = "2.7.7"
        val room_version = "2.6.1"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
        classpath ("org.jetbrains.kotlin:kotlin-android-extensions:$kotlin_version")
    }
}