plugins {
    id("dev.reactant.gradle") apply true
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    shadow(project(":reactant-command"))
}

tasks.generateSpigotPluginConfig {
    spigotPluginConfig {
        main = "dev.reactant.devbridge.ReactantDevelopmentBridgePlugin"

        command("reactant-development") {
            aliases = arrayOf("reactant-dev", "rdev")
        }
    }
}
