dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    testImplementation("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    api("info.picocli:picocli:4.6.1")
}

tasks.generateSpigotPluginConfig { enabled = false }
tasks.generateReactantPackageInfo { enabled = false }
