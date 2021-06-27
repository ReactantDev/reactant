package dev.reactant.gradle

import kotlinx.serialization.Serializable
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

@Serializable
class SpigotPluginCommand {
    @Input
    @Optional
    var description: String? = null

    @Input
    @Optional
    var aliases: Array<String>? = null

    @Input
    @Optional
    var permission: String? = null

    @Input
    @Optional
    var `permission-message`: String? = null
}

@Serializable
class SpigotPluginPermission {
    @Input
    @Optional
    var description: String? = null

    @Input
    @Optional
    var default: String? = null

    @Input
    @Optional
    var children: HashMap<String, Boolean>? = null
}

@Serializable
class SpigotPluginConfig {
    @Input
    @Optional
    var name: String? = null

    @Input
    @Optional
    var version: String? = null

    @Input
    @Optional
    var main: String? = null

    @Input
    @Optional
    var description: String? = null

    @Input
    @Optional
    var `api-version`: String? = null

    @Input
    @Optional
    var load: String? = null

    @Input
    @Optional
    var author: String? = null

    @Input
    @Optional
    var authors: Array<String>? = null

    @Input
    @Optional
    var website: String? = null

    @Input
    @Optional
    var depend: Array<String>? = null

    @Input
    @Optional
    var softdepend: Array<String>? = null

    @Input
    @Optional
    var loadbefore: Array<String>? = null

    @Input
    @Optional
    var prefix: String? = null

    @Input
    @Optional
    var libraries: Array<String>? = null

    @Nested
    @Optional
    var commands: HashMap<String, SpigotPluginCommand>? = null

    @Nested
    @Optional
    var permissions: HashMap<String, SpigotPluginPermission>? = null

    inner class SpigotPluginConfigDSL {
        var name: String? by this@SpigotPluginConfig::name
        var version: String? by this@SpigotPluginConfig::version
        var main: String? by this@SpigotPluginConfig::main
        var description: String? by this@SpigotPluginConfig::description
        var apiVersion: String? by this@SpigotPluginConfig::`api-version`
        var load: String? by this@SpigotPluginConfig::load
        var author: String? by this@SpigotPluginConfig::author
        var authors: Array<String>? by this@SpigotPluginConfig::authors
        var website: String? by this@SpigotPluginConfig::website
        var depend: Array<String>? by this@SpigotPluginConfig::depend
        var softDepend: Array<String>? by this@SpigotPluginConfig::softdepend
        var loadBefore: Array<String>? by this@SpigotPluginConfig::loadbefore
        var prefix: String? by this@SpigotPluginConfig::prefix

        fun command(name: String, content: SpigotPluginCommand.() -> Unit) {
            if (this@SpigotPluginConfig.commands == null) this@SpigotPluginConfig.commands = hashMapOf()
            this@SpigotPluginConfig.commands!![name] = SpigotPluginCommand().apply(content)
        }

        fun permission(name: String, content: SpigotPluginPermission.() -> Unit) {
            if (this@SpigotPluginConfig.permissions == null) this@SpigotPluginConfig.permissions = hashMapOf()
            this@SpigotPluginConfig.permissions!![name] = SpigotPluginPermission().apply(content)
        }
    }

    operator fun invoke(content: SpigotPluginConfigDSL.() -> Unit) {
        SpigotPluginConfigDSL().apply(content)
    }
}
