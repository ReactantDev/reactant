package dev.reactant.gradle

abstract class ReactantDependencyExtension(private val gradlePluginVersion: String) {
    operator fun invoke(name: String, version: String? = gradlePluginVersion): String {
        return "dev.reactant:reactant-$name:$version"
    }
}
