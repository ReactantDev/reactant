rootProject.name = "reactant"
pluginManagement {
    includeBuild("reactant-gradle-plugin")
    repositories {
        flatDir { dir("./reactant-gradle-plugin/build/libs") }
        mavenLocal()
        gradlePluginPortal()
    }
}
include("reactant-development-bridge")
include("reactant-intellij-plugin")
include("reactant-package-manager")
include("reactant-di")
include("reactant-event-stream")
include("reactant-config")
include("reactant-command")
include("reactant-serialization-spigot")
include("reactant-example-plugin")
