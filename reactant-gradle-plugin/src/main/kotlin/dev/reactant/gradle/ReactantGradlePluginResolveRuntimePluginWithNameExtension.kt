package dev.reactant.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency

abstract class ReactantGradlePluginResolveRuntimePluginWithNameExtension(
    private val project: Project,
    private val pluginDependencyNotationNameMap: HashMap<String, String>,
) {
    operator fun invoke(dependencyNotation: String, pluginName: String): Dependency? =
        project.dependencies.add(RESOLVE_RUNTIME_PLUGIN_CONFIGURATION_NAME, dependencyNotation)
            ?.apply { pluginDependencyNotationNameMap["$group:$version"] = pluginName }
}
