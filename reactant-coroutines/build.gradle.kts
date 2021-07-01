dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    testImplementation("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")

    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
}

tasks.generateSpigotPluginConfig { enabled = false }
tasks.generateReactantPackageInfo { enabled = false }
