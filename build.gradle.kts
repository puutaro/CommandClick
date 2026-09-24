    // Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val kotlinVersion = "2.2.10"
//    "1.9.25"
    id("com.android.application") version "9.4.1" apply false
    id("com.android.library") version "9.4.1" apply false
    id("org.jetbrains.kotlin.android") version kotlinVersion apply false
//    id("com.chaquo.python") version "15.0.0" apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
//        kotlinOptions.jvmTarget = "17"
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }

    }
}