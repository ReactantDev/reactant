package dev.reactant.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion

abstract class ReactantPluginPreconfiguredTestingExtension(
    private val project: Project
) {
    operator fun invoke(
        junitVersion: String = "5.7.2",
        mockitoVersion: String = "3.11.2",
        jacocoVersion: String = "0.8.7",
    ){
        project.dependencies.apply {
            add(
                RESOLVE_RUNTIME_LIBRARY_CONFIGURATION_NAME,
                "org.jetbrains.kotlin:kotlin-stdlib:${project.getKotlinPluginVersion()}"
            )

            listOf(
                platform("org.junit:junit-bom:${junitVersion}"),
                "org.junit.jupiter:junit-jupiter",
                "org.mockito:mockito-core:${mockitoVersion}",
                "org.mockito:mockito-junit-jupiter:${mockitoVersion}",
                "org.mockito:mockito-inline:${mockitoVersion}",
            ).forEach { add("testImplementation", it) }
        }

        (project.tasks.getByName("test") as Test).apply {
            useJUnitPlatform()
            testLogging {
                it.events("passed", "skipped", "failed")
            }
        }

        (project.extensions.getByName("jacoco") as JacocoPluginExtension).apply {
            toolVersion = jacocoVersion
        }
    }
}
