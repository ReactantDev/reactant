plugins {
    id("org.jetbrains.intellij") version "1.0"
    kotlin("jvm")
}

group = "dev.reactant"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("2021.1.1")
    type.set("IC")
    plugins.set(
        listOf("com.intellij.java", "org.jetbrains.plugins.gradle")
    )
}
tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes.set("Add change notes here.<br><em>most HTML tags may be used</em>")
}

tasks.generateSpigotPluginConfig { enabled = false }
tasks.generateReactantPackageInfo { enabled = false }
