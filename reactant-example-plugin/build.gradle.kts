dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    implementation(project(":reactant-command"))
}

tasks.generateSpigotPluginConfig {
    spigotPluginConfig {
        main = "dev.reactant.example.ReactantExamplePlugin"

        command("reactant-example") {
        }
    }
}
