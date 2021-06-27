package dev.reactant.command

import dev.reactant.command.exceptions.CommandNameNotFoundException
import org.bukkit.command.CommandExecutor
import org.bukkit.plugin.java.JavaPlugin
import picocli.CommandLine

inline fun <reified T : ReactantCommand> JavaPlugin.registerReactantCommand(
    noinline commandCreator: () -> T,
    executorCreator: (
        commandSpec: CommandLine.Model.CommandSpec,
        commandCreator: () -> T
    ) -> CommandExecutor = ::ReactantCommandExecutor
) {
    val commandSpec = CommandLine.Model.CommandSpec.forAnnotatedObject(T::class.java)
    getCommand(commandSpec.name())
        .also { if (it == null) throw CommandNameNotFoundException(commandSpec) }
        ?.setExecutor(executorCreator(commandSpec, commandCreator))
}
