plugins {
    val kotlinVersion: String = System.getProperty("kotlinVersion")
    kotlin("jvm") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    `maven-publish`
    id("dev.reactant.gradle") version System.getProperty("reactantVersion")
}

val reactantVersion = System.getProperty("reactantVersion")
group = "dev.reactant"

allprojects {
    group = "dev.reactant"
    repositories {
        mavenCentral()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.gradle.maven-publish")

    version = reactantVersion

    ktlint {
        enableExperimentalRules.set(true)
        disabledRules.set(setOf("no-wildcard-imports"))
    }
}

ktlint {
    version.set("0.40.0")
}

tasks.generateReactantPackageInfo { enabled = false }
tasks.generateSpigotPluginConfig { enabled = false }

tasks.jacocoTestReport {
    setOnlyIf { true }
    additionalSourceDirs.setFrom(subprojects.map { it.sourceSets.main.get().allSource.srcDirs })
    sourceDirectories.setFrom(subprojects.map { it.sourceSets.main.get().allSource.srcDirs })
    classDirectories.setFrom(subprojects.map { it.sourceSets.main.get().output })
    executionData.setFrom({ project.fileTree(".") { include("**/build/jacoco/test.exec") } })

    reports {
        xml.isEnabled = true
        xml.destination = file("$buildDir/reports/jacoco/report.xml")
    }
}

afterEvaluate {
    tasks.jacocoTestReport.get().dependsOn(
        subprojects.mapNotNull {
            if (it.pluginManager.hasPlugin("jacoco")) it.tasks.jacocoTestReport else null
        }
    )
}
