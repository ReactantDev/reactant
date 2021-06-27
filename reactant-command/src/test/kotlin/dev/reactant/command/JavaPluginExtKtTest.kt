package dev.reactant.command

import org.bukkit.command.PluginCommand
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import picocli.CommandLine

internal class JavaPluginExtKtTest {
    @Test
    fun registerReactantCommand() {
        @CommandLine.Command(name = "testing")
        class TestingCommand : ReactantCommand() {
            override fun run() {
            }
        }

        val mockedJavaPlugin = mock(JavaPlugin::class.java)
        val mockedBukkitPluginCommand = mock(PluginCommand::class.java)
        `when`(mockedJavaPlugin.getCommand("testing")).thenReturn(mockedBukkitPluginCommand)

        mockedJavaPlugin.registerReactantCommand(::TestingCommand)

        verify(mockedBukkitPluginCommand, times(1))
            .setExecutor(argThat { it is ReactantCommandExecutor && it.reactantCommandCreator() is TestingCommand })
    }
}
