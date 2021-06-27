package dev.reactant.example.commands

import dev.reactant.command.ReactantCommand
import picocli.CommandLine

@CommandLine.Command(
    name = "reactant-example",
    aliases = ["reactantexample"],
    description = ["Example Command"]
)
class ReactantExampleCommand : ReactantCommand() {
    @CommandLine.Option(names = ["-x"])
    var test: Boolean = false

    override fun run() {
        sender.sendMessage("Hi")
    }
}
