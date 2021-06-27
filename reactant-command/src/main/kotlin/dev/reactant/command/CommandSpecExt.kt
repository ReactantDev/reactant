package dev.reactant.command

import picocli.CommandLine

internal fun CommandLine.Model.CommandSpec.getPermission(): String? =
    userObject()::class.java.getAnnotation(CommandPermission::class.java)?.permission
