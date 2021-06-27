package dev.reactant.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import picocli.CommandLine

internal class CommandSpecExtKtTest {

    @CommandLine.Command(name = "testing")
    @CommandPermission("dev.reactant.testing")
    class TestingCommand : ReactantCommand() {
        override fun run() {
        }
    }

    @Test
    fun getPermission_commandSpecFromClass() {
        val actual = CommandLine.Model.CommandSpec.forAnnotatedObject(TestingCommand::class.java).getPermission()
        val expected = "dev.reactant.testing"
        assertEquals(expected, actual)
    }

    @Test
    fun getPermission_commandSpecFromObject() {
        val actual = CommandLine.Model.CommandSpec.forAnnotatedObject(TestingCommand()).getPermission()
        val expected = "dev.reactant.testing"
        assertEquals(expected, actual)
    }
}
