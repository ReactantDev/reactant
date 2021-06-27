package dev.reactant.gradle

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

@Serializable
class ReactantPackageInfo(
    val libraries: List<String>,
    val plugins: Map<String, String>
)

abstract class ReactantPackageGenerateInfoTask @Inject constructor(
    private val pluginDependencyNotationNameMap: Map<String, String>
) : DefaultTask() {
    @get:OutputFile
    abstract val packageInfoFile: RegularFileProperty

    @get:Input
    abstract val resourcesPath: Property<String>

    init {
        packageInfoFile.convention { File("${resourcesPath.get()}/reactant-package.json") }
    }

    private val json = Json { prettyPrint = true }

    @TaskAction
    fun generatePackageInfoResources() {
        val packageInfo = ReactantPackageInfo(
            libraries = project.configurations.getByName(RESOLVE_RUNTIME_LIBRARY_CONFIGURATION_NAME).dependencies
                .map { "${it.group}:${it.name}:${it.version}" },
            plugins = project.configurations.getByName(RESOLVE_RUNTIME_PLUGIN_CONFIGURATION_NAME).dependencies.associate {
                val ref = "${it.group}:${it.name}"
                pluginDependencyNotationNameMap[ref]!! to "$ref:${it.version}"
            }
        )
        packageInfoFile
            .asFile.get().writeText(
                json.encodeToString(packageInfo)
            )
    }
}
