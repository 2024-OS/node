// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.gms.google.services) apply false
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin) apply false

    // Kotlin 버전을 2.0.0으로 상향하여 캐시된 2.2.0 라이브러리들과의 호환성을 맞춥니다.
    id("org.jetbrains.kotlin.android") version "2.2.0" apply false
    id("org.jetbrains.kotlin.kapt") version "2.2.0" apply false
}

buildscript {
    dependencies {
        // 이 부분은 그대로 둡니다.
        classpath("com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1")
    }
}