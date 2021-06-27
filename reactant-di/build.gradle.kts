dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
}

tasks.generateSpigotPluginConfig {
    spigotPluginConfig {
        main = "dev.reactant.di.ReactantDependencyInjection"
        softDepend = arrayOf("reactant-package-manager")
    }
}
