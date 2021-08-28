package dev.reactant.example

import dev.reactant.command.registerReactantCommand
import dev.reactant.example.commands.ReactantExampleCommand
import org.bukkit.plugin.java.JavaPlugin

class ReactantExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        instance = this
        registerReactantCommand(::ReactantExampleCommand)
    }

    companion object {
        lateinit var instance: ReactantExamplePlugin
    }
}

internal val plugin get() = ReactantExamplePlugin.instance
