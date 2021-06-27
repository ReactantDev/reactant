dependencies {
    compileOnly("org.spigotmc:spigot-api:${System.getProperty("spigotAPIVersion")}")
    shadow("org.apache.maven:maven-resolver-provider:3.6.3")
    shadow("org.apache.maven.resolver:maven-resolver-api:1.7.0")
    shadow("org.apache.maven.resolver:maven-resolver-impl:1.7.0")
    shadow("org.apache.maven.resolver:maven-resolver-connector-basic:1.7.0")
    shadow("org.apache.maven.resolver:maven-resolver-transport-file:1.7.0")
    shadow("org.apache.maven.resolver:maven-resolver-transport-http:1.7.0")

    testImplementation("org.apache.maven:maven-resolver-provider:3.6.3")
    testImplementation("org.apache.maven.resolver:maven-resolver-api:1.7.0")
    testImplementation("org.apache.maven.resolver:maven-resolver-impl:1.7.0")
    testImplementation("org.apache.maven.resolver:maven-resolver-connector-basic:1.7.0")
    testImplementation("org.apache.maven.resolver:maven-resolver-transport-file:1.7.0")
    testImplementation("org.apache.maven.resolver:maven-resolver-transport-http:1.7.0")
    resolveRuntimeLibrary("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
}

tasks.generateSpigotPluginConfig {
    spigotPluginConfig {
        main = "dev.reactant.pm.ReactantPackageManager"

        permission("reactant.pm") {
            description = "reactant package manager permission"
            default = "false"
        }

        command("reactant-package-manager") {
            description = "reactant package manager command"
            aliases = arrayOf("rpm", "reactantpm")
            permission = "reactant.pm"
        }
    }
}
