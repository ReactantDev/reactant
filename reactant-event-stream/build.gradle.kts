dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
}

tasks.generateSpigotPluginConfig { enabled = false }
tasks.generateReactantPackageInfo { enabled = false }
