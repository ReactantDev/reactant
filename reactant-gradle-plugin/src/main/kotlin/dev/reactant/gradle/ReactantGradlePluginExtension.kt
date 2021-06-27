package dev.reactant.gradle

import org.gradle.api.provider.Property

abstract class ReactantGradlePluginExtension {
    abstract val preconfigureJunit: Property<Boolean>
    abstract val preconfigureJunitVersion: Property<String>
    abstract val preconfigureMockitoVersion: Property<String>

    init {
        preconfigureJunit.convention(true)
        preconfigureJunitVersion.convention("5.7.2")
        preconfigureMockitoVersion.convention("3.11.2")
    }
}
