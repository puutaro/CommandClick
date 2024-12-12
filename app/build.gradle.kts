
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

val applicationName = "CommandClick"
val versionMajor = 1
val versionMinor = 4
val versionPatch = 2

android {
    namespace = "com.puutaro.commandclick"
    compileSdk = 35

    packaging {
        resources {
            excludes.add("META-INF/INDEX.LIST")
            excludes.add("META-INF/io.netty.versions.properties")
            jniLibs {
                useLegacyPackaging = false
            }
        }
    }


    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.puutaro.commandclick"
        minSdk = 27
        targetSdk = 33
        versionCode = 82
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        base.archivesName.set("${applicationName}-${versionName}")
        buildTypes {
            debug {
                buildConfigField("boolean", "DEBUG", "true")
            }
            release {
                buildConfigField("boolean", "DEBUG", "false")
                isMinifyEnabled = false
                proguardFiles(getDefaultProguardFile(
                    "proguard-android-optimize.txt"),
                    "proguard-rules.pro"
                )
            }
        }
//        ndk {
//            abiFilters.add("x86")
//            abiFilters.add("x86_64")
//            abiFilters.add("'armeabi'")
//            abiFilters.add("armeabi-v7a")
//            abiFilters.add("arm64-v8a")
////            abiFilters += listOf("arm64-v8a", "x86_64")
////                listOf("armeabi-v7a", "arm64-v8a")
//        }
    }




    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
            //JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_17
            //JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    //"1.8"
    }
//    buildFeatures.compose = true
    dataBinding {
        enable = true
    }

//    splits {
//        abi {
//            isEnable = true
//            reset()
//            include( "armeabi", "arm64-v8a", "x86")
////             "x86", "x86_64", "armeabi-v7a",
//            isUniversalApk = true
//        }
//    }
}


//ext.architectures = ["armeabi-v7a", "arm64-v8a", "x86", "x86_64"]
////ext.libDir = "$project.projectDir/src/main/resources/lib"
//ext.libDir = "$project.projectDir/src/main/jniLibs"
extra["architectures"] = listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
//ext.architectures = ["armeabi-v7a", "arm64-v8a", "x86", "x86_64"]
//ext.libDir = "$project.projectDir/src/main/resources/lib"
extra["libDir"] = "${project.projectDir}/src/main/jniLibs"



dependencies {

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.test:core-ktx:1.6.1")
    implementation("androidx.fragment:fragment-ktx:1.8.5")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.termux.termux-app:termux-shared:0.117")
    implementation("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("net.yslibrary.keyboardvisibilityevent:keyboardvisibilityevent:3.0.0-RC3")
    implementation("com.jakewharton.threetenabp:threetenabp:1.2.1")
    implementation("commons-io:commons-io:2.13.0")
    implementation("com.github.omadahealth:swipy:1.2.3@aar")
    implementation("com.github.skydoves:colorpickerview:2.2.4")
    implementation("com.anggrayudi:storage:1.5.5")
    implementation("com.github.sya-ri:kgit:1.0.5")
    implementation("com.github.abumoallim:Android-Multi-Select-Dialog:v1.9")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.0")
    implementation("org.jsoup:jsoup:1.7.3")
    implementation("com.itextpdf:itextg:5.5.10")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.11.0")
    implementation("com.github.bachors:Android-Img2Ascii:1.1")
    implementation("org.rauschig:jarchivelib:0.8.0")
//    implementation("org.apache.commons:commons-compress:1.26.2")
    implementation("com.github.mwiede:jsch:0.2.11")

    implementation("com.github.yuriy-budiyev:code-scanner:2.3.2")
    implementation("io.github.thanosfisherman.wifiutils:wifiutils:1.6.6")

    implementation ("com.github.alexzhirkevich:custom-qr-generator:2.0.0-alpha01")

    implementation("io.coil-kt:coil:2.4.0")
//    implementation("com.github.maxrave-dev:kotlin-youtubeExtractor:0.0.7")
    implementation("com.blankj:utilcodex:1.31.1")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
//    implementation("io.ktor:ktor-server-netty:1.4.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.gleb8k:Android-PathShapeView:1.3.2")
    implementation("com.daimajia.androidanimations:library:2.4@aar")
    val htextview_version = "0.1.2"
//    implementation("com.github.kibotu:htextview:0.1.28")
    implementation("com.github.takusemba:spotlight:2.0.5")
    implementation("jp.co.cyberagent.android:gpuimage:2.1.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("net.objecthunter:exp4j:0.4.8")
// as 3.0 use implementation
//    implementation ("com.hanks:htextview-base:0.1.6" )       // base library

//    implementation( "com.hanks:htextview-fade:$htextview_version")        // optional
//    implementation ("com.hanks:htextview-line:$htextview_version")        // optional
//    implementation ("com.hanks:htextview-rainbow:$htextview_version")     // optional
//    implementation ("com.hanks:htextview-typer:$htextview_version")       // optional
//
////    implementation ("com.hanks:htextview-scale:$htextview_version")       // optional
//    implementation ("com.hanks:htextview-evaporate:$htextview_version")   // optional
//    implementation ("com.hanks:htextview-fall:$htextview_version" )       // optional

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")



//    implementation("androidx.camera:camera-camera2:1.2.2")
//    implementation("androidx.camera:camera-lifecycle:1.2.2")
//    implementation("androidx.camera:camera-view:1.2.2")

//    implementation("org.nanohttpd:nanohttpd:2.2.0")
}
