import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
    //id("com.github.johnrengelman.shadow") version "latest.release" // use packageUberJarForCurrentOS
}

group = "me.guest_3slo32w"
version = "1.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines:0.19.2")
    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    //implementation("com.arkivanov.decompose:decompose:0.5.2")
    //implementation("com.arkivanov.decompose:extensions-compose-jetbrains:latest.release")
    //implementation("org.jetbrains.compose.material:material-icons-extended:${"1.1.1"}") // 30MB 大包
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    // "Kotlin source files are always UTF-8 by design."  THAT'S GOOD!
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}



/*
tasks {
    shadowJar {
        manifest {
            attributes(mapOf(
                "Main-Class" to "MainKt",              //will make your jar (produced by jar task) runnable
                "ImplementationTitle" to project.name,
                "Implementation-Version" to project.version)
            )
        }
    }
}
*/

compose.desktop {
    application {
        mainClass = "MainKt"
        jvmArgs += listOf()//"-Xmx512m","-Xms32m","-XX:+UseZGC","-Dfile.encoding=gbk")
        args += listOf()
        description = "Compose Example App"
        nativeDistributions {
            //includeAllModules = true
            targetFormats(TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Dmg)
            packageName = "composePlay2"
            packageVersion = "1.0.0"
            copyright = "©Copyright 2020-2021 My Name. All rights reserved."
            vendor = "Example vendor"
            licenseFile.set(project.file("LICENSE.txt"))
            windows {
                upgradeUuid = "2dc6921b-521a-4c3a-9e9e-fe9488e56e14"
                dirChooser = true
                shortcut = true
                msiPackageVersion = "0.1.0"
                iconFile.set(project.file("icon.ico"))
            }
            linux {
                shortcut = true
                packageName = "test-composeplay2"
                //iconFile.set(project.file("icon.png"))
            }
            macOS {
                //iconFile.set(project.file("icon.icns"))
            }


        }
    }
}