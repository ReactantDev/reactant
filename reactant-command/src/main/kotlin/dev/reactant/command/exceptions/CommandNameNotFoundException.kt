package dev.reactant.command.exceptions

import picocli.CommandLine

class CommandNameNotFoundException(commandSpec: CommandLine.Model.CommandSpec) : RuntimeException(
    "Command ${commandSpec.name()} not found, have you declared it in plugin.yml?"
)
