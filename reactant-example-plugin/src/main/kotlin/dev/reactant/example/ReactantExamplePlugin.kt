package dev.reactant.example

import dev.reactant.command.registerReactantCommand
import dev.reactant.example.commands.ReactantExampleCommand
import org.bukkit.plugin.java.JavaPlugin

class ReactantExamplePlugin : JavaPlugin() {
    override fun onEnable() {
        registerReactantCommand(::ReactantExampleCommand)
    }
}
