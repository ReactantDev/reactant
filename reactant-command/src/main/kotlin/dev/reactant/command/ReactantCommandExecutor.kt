package dev.reactant.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import picocli.AutoComplete
import picocli.CommandLine

internal val tabCompletingCommandLine = ThreadLocal<ReactantCommand>()

open class ReactantCommandExecutor(
    val commandSpec: CommandLine.Model.CommandSpec,
    val reactantCommandCreator: () -> ReactantCommand,
) : TabExecutor {
    /**
     * Re-separate the bukkit command args input into args
     * Support quoting space and backslash
     */
    fun argumentsReSeparator(args: Array<out String>): Array<out String> {
        val originalString = args.joinToString(" ")
        val result = arrayListOf<String>()
        var quoted = false
        var backslashed = false
        val currentArg = StringBuilder()
        originalString.forEach {
            if (backslashed) {
                currentArg.append(it)
                backslashed = false
            } else when (it) {
                '"' -> quoted = !quoted
                '\\' -> backslashed = true
                ' ' -> {
                    if (quoted) {
                        currentArg.append(it)
                    } else {
                        if (currentArg.isNotEmpty()) {
                            result.add(currentArg.toString())
                            currentArg.clear()
                        }
                    }
                }
                else -> currentArg.append(it)
            }
        }
        if (currentArg.isNotEmpty()) result.add(currentArg.toString())
        return result.toTypedArray()
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): MutableList<String>? {
        val candidates = arrayListOf<String>()
        val permission = commandSpec.getPermission()
        if (permission != null && !sender.hasPermission(permission)) return candidates

        // TODO: validate all permissions including subcommands

        val reSeparatedArgs = argumentsReSeparator(args)
        val enteringNewArg = args.lastOrNull()?.lastOrNull() == ' '
        if (reSeparatedArgs.size == 0 && !enteringNewArg) return candidates

        val reactantCommand = reactantCommandCreator()
        reactantCommand.sender = sender
        runCatching { CommandLine(reactantCommand).parseArgs(*reSeparatedArgs) }
        tabCompletingCommandLine.set(reactantCommand)

        AutoComplete.complete(
            commandSpec,
            reSeparatedArgs,
            reSeparatedArgs.size + (if (enteringNewArg) 0 else -1),
            reSeparatedArgs.lastOrNull()?.length ?: 0,
            0,
            candidates as List<String>,
        )

        tabCompletingCommandLine.remove()

        return candidates.map { (args.lastOrNull() ?: "") + it }.toMutableList()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val reactantCommand = reactantCommandCreator()
        val reSeparatedArgs = argumentsReSeparator(args)
        reactantCommand.sender = sender
        CommandLine(reactantCommand).execute(*reSeparatedArgs)
        return true
    }
}
