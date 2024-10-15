// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.7.1" apply false
    id("com.android.library") version "8.7.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
//    id("com.chaquo.python") version "15.0.0" apply false
}

subprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        compilerOptions.jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
//        kotlinOptions.jvmTarget = "17"
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }

    }
}