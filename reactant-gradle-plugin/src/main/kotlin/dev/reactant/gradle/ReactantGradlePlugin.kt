package dev.reactant.gradle

import com.github.jengelman.gradle.plugins.shadow.ShadowExtension
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jlleitschuh.gradle.ktlint.KtlintExtension

internal const val RESOLVE_RUNTIME_LIBRARY_CONFIGURATION_NAME = "resolveRuntimeLibrary"
internal const val RESOLVE_RUNTIME_PLUGIN_CONFIGURATION_NAME = "resolveRuntimePlugin"

class ReactantGradlePlugin : Plugin<Project> {

    private fun applyPlugins(project: Project) {
        project.pluginManager.apply("jacoco")
        project.pluginManager.apply("org.jetbrains.kotlin.jvm")
        project.pluginManager.apply("com.github.johnrengelman.shadow")
        project.pluginManager.apply("org.jlleitschuh.gradle.ktlint")
        project.pluginManager.apply("org.gradle.maven-publish")
        project.pluginManager.apply("com.palantir.git-version")

        val jacocoReport = (project.tasks.getByName("jacocoTestReport") as JacocoReport).apply {
            reports {
                it.xml.isEnabled = true
                it.html.isEnabled = false
            }
        }

        project.tasks.getByName("check").dependsOn(jacocoReport)
    }

    /**
     * Replace jar task with shadowJar task
     * Only shadow dependencies with "shadow" configuration
     */
    private fun configureArchives(project: Project) {
        val shadowJar = (project.tasks.getByName("shadowJar") as ShadowJar).apply {
            archiveClassifier.set(null as String?)
            configurations = listOf(
                project.configurations.getByName("shadow")
            )
        }

        (project.tasks.getByName("jar") as Jar).apply {
            enabled = false
            dependsOn(shadowJar)
        }
    }

    private fun registerTasks(project: Project, pluginDependencyNotationNameMap: HashMap<String, String>) {
        val resourcePath = "${project.buildDir}/resources/main"

        val reactantPackageGenerateSpigotPluginConfigTask = project.tasks.register(
            "generateSpigotPluginConfig",
            ReactantPackageGenerateSpigotPluginConfigTask::class.java,
        )
        reactantPackageGenerateSpigotPluginConfigTask.configure {
            it.resourcesPath.set(resourcePath)
        }

        val reactantPackageGeneratePackageInfoTask = project.tasks.register(
            "generateReactantPackageInfo",
            ReactantPackageGenerateInfoTask::class.java,
            pluginDependencyNotationNameMap,
        )
        reactantPackageGeneratePackageInfoTask.configure {
            it.resourcesPath.set(resourcePath)
        }

        val reactantBridgeTransferPlugin = project.tasks
            .register("reactantBridgeTransferPlugin")
            .configure { it.dependsOn(project.tasks.getByName("build")) }
        project.tasks
            .register("reactantBridgeReloadServer")
            .configure { it.dependsOn(reactantBridgeTransferPlugin) }

        project.tasks.getByName("processResources").dependsOn(
            reactantPackageGenerateSpigotPluginConfigTask.get(),
            reactantPackageGeneratePackageInfoTask.get(),
        )
    }

    private fun configureKtlint(project: Project) {
        project.extensions.configure(KtlintExtension::class.java) {
            it.disabledRules.set(setOf("no-wildcard-imports"))
        }
    }

    private fun configureMavenPublishing(project: Project) {
        project.extensions.configure(PublishingExtension::class.java) {
            it.publications {
                it.maybeCreate("maven", MavenPublication::class.java).let {
                    (project.extensions.getByName("shadow") as ShadowExtension).component(it)

                    it.groupId = project.group.toString()
                    it.artifactId = project.name
                    it.version = project.version.toString()
                }
            }
        }
    }

    override fun apply(project: Project) {

        val reactantVersion = this::class.java.`package`.implementationVersion

        project.repositories.mavenCentral()

        applyPlugins(project)

        project.dependencies.extensions.create("reactant", ReactantDependencyExtension::class.java, reactantVersion)

        val resolveRuntimeLibrary = project.configurations.create(RESOLVE_RUNTIME_LIBRARY_CONFIGURATION_NAME)
        project.configurations.getByName("compileOnly").extendsFrom(resolveRuntimeLibrary)
        project.configurations.getByName("testImplementation").extendsFrom(resolveRuntimeLibrary)

        val resolveRuntimePlugin = project.configurations.create(RESOLVE_RUNTIME_PLUGIN_CONFIGURATION_NAME)
        project.configurations.getByName("compileOnly").extendsFrom(resolveRuntimePlugin)
        project.configurations.getByName("testImplementation").extendsFrom(resolveRuntimePlugin)

        val pluginDependencyNotationNameMap: HashMap<String, String> = hashMapOf()

        project.extensions.create(
            "resolveRuntimePluginWithName",
            ReactantGradlePluginResolveRuntimePluginWithNameExtension::class.java,
            project,
            pluginDependencyNotationNameMap
        )

        val reactantGradleExtension =
            project.extensions.create("reactantGradle", ReactantGradlePluginExtension::class.java)

        project.extensions.create("getTaggedVersion", ReactantTaggedVersionExtension::class.java, project)

        configureMavenPublishing(project)

        registerTasks(project, pluginDependencyNotationNameMap)

        configureKtlint(project)

        configureArchives(project)

        project.afterEvaluate {
            if (reactantGradleExtension.preconfigureJunit.get()) {
                project.dependencies.apply {
                    add(
                        RESOLVE_RUNTIME_LIBRARY_CONFIGURATION_NAME,
                        "org.jetbrains.kotlin:kotlin-stdlib:${it.getKotlinPluginVersion()}"
                    )

                    listOf(
                        platform("org.junit:junit-bom:${reactantGradleExtension.preconfigureJunitVersion.get()}"),
                        "org.junit.jupiter:junit-jupiter",
                        "org.mockito:mockito-core:${reactantGradleExtension.preconfigureMockitoVersion.get()}",
                        "org.mockito:mockito-junit-jupiter:${reactantGradleExtension.preconfigureMockitoVersion.get()}",
                        "org.mockito:mockito-inline:${reactantGradleExtension.preconfigureMockitoVersion.get()}",
                    ).forEach { add("testImplementation", it) }
                }

                (project.tasks.getByName("test") as Test).apply {
                    useJUnitPlatform()
                    testLogging {
                        it.events("passed", "skipped", "failed")
                    }
                }

                (project.extensions.getByName("jacoco") as JacocoPluginExtension).apply {
                    toolVersion = "0.8.7"
                }
            }
        }
    }
}
