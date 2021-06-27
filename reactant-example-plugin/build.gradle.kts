plugins {
    id("dev.reactant.gradle") apply true
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    shadow(project(":reactant-command"))
}

tasks.generateSpigotPluginConfig {
    spigotPluginConfig {
        main = "dev.reactant.example.ReactantExamplePlugin"

        command("reactant-example") {
        }
    }
}
