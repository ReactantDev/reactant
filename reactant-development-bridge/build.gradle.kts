val ktorVersion = "1.6.0"

plugins {
    kotlin("plugin.serialization") version System.getProperty("kotlinVersion")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    resolveRuntimeLibrary("io.ktor:ktor-server-core:$ktorVersion")
    resolveRuntimeLibrary("io.ktor:ktor-server-netty:$ktorVersion")
    resolveRuntimeLibrary("io.ktor:ktor-serialization:$ktorVersion")
    resolveRuntimeLibrary("org.kodein.di:kodein-di-jvm:7.6.0")
    resolveRuntimeLibrary("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation(project(":reactant-coroutines"))
    implementation(project(":reactant-command"))
}

tasks.generateSpigotPluginConfig {
    spigotPluginConfig {
        main = "dev.reactant.devbridge.ReactantDevelopmentBridgePlugin"

        command("reactant-development") {
            aliases = arrayOf("reactant-dev", "rdev")
        }
    }
}
