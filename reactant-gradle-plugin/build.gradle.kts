version = System.getProperty("reactantVersion") as String
group = "dev.reactant"

plugins {
    `java-gradle-plugin`
    val kotlinVersion: String = System.getProperty("kotlinVersion")
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    `maven-publish`
}

val kotlinVersion: String = System.getProperty("kotlinVersion")
val reactantVersion: String = System.getProperty("reactantVersion")

repositories {
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("com.charleskorn.kaml:kaml:0.34.0")
    api("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    api("gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0")
    api("org.jlleitschuh.gradle:ktlint-gradle:10.1.0")
    api("com.palantir.gradle.gitversion:gradle-git-version:0.12.3")
    api("com.gradle.publish:plugin-publish-plugin:0.15.0")
}

gradlePlugin {
    plugins {
        create("reactantPackageGradlePlugin") {
            id = "dev.reactant.gradle"
            implementationClass = "dev.reactant.gradle.ReactantGradlePlugin"
        }
    }
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Version" to reactantVersion
        )
    }
}
