package dev.reactant.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class ReactantBridgeTask : DefaultTask() {

    @get:Input
    abstract val bridgeHost: Property<String>

    init {
        @Suppress("LeakingThis")
        bridgeHost.convention("http://localhost:38230")
    }
}
