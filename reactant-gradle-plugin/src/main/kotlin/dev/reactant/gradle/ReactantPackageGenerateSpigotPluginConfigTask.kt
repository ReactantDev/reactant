package dev.reactant.gradle

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.encodeToString
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

abstract class ReactantPackageGenerateSpigotPluginConfigTask @Inject constructor() : DefaultTask() {
    @get:OutputFile
    abstract val pluginConfigFile: RegularFileProperty

    @get:Input
    abstract val resourcesPath: Property<String>

    @Nested
    val spigotPluginConfig: SpigotPluginConfig = SpigotPluginConfig()

    init {
        pluginConfigFile.convention { File("${resourcesPath.get()}/plugin.yml") }
    }

    private val yaml = Yaml(configuration = YamlConfiguration(encodeDefaults = false))

    @TaskAction
    fun generateSpigotPluginConfigResources() {
        spigotPluginConfig.name = spigotPluginConfig.name ?: project.name
        spigotPluginConfig.version = spigotPluginConfig.version ?: project.version.toString()
        spigotPluginConfig.libraries = project.configurations.getByName(RESOLVE_RUNTIME_LIBRARY_CONFIGURATION_NAME).dependencies
            .map { "${it.group}:${it.name}:${it.version}" }.toTypedArray()
        pluginConfigFile
            .asFile.get().writeText(
                yaml.encodeToString(spigotPluginConfig)
            )
    }
}
