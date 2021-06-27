package dev.reactant.command

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import picocli.CommandLine

internal class ReactantCommandExecutorTest {
    lateinit var blankCommandExecutor: ReactantCommandExecutor
    lateinit var argsCommandExecutor: ReactantCommandExecutor

    @BeforeEach
    fun setupBlankCommandExecutor() {
        blankCommandExecutor =
            ReactantCommandExecutor(CommandLine.Model.CommandSpec.create()) { mock(ReactantCommand::class.java) }
    }

    @CommandLine.Command(name = "testing")
    class ArgsTestingCommand : ReactantCommand() {
        @CommandLine.Option(names = ["--name", "-n"])
        var name: String? = null

        @CommandLine.Option(names = ["--age", "-a"])
        var age: String? = null

        override fun run() {
        }
    }

    @BeforeEach
    fun setupArgsCommandExecutor() {
        argsCommandExecutor = ReactantCommandExecutor(
            CommandLine.Model.CommandSpec.forAnnotatedObject(ArgsTestingCommand::class.java),
            ::ArgsTestingCommand
        )
    }

    @Test
    fun argumentsReSeparator_withEmptyInput() {
        val input = """""".split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf<String>()
        assertArrayEquals(expected, actual)
    }

    @Test
    fun argumentsReSeparator_withSpaceOnlyInput() {
        val input = """     """.split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf<String>()
        assertArrayEquals(expected, actual)
    }

    @Test
    fun argumentsReSeparator_withMultipleSpaceOnlyArgsInput() {
        val input = """"  " "   " \  " """".split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf("  ", "   ", " ", " ")
        assertArrayEquals(expected, actual)
    }

    @Test
    fun argumentsReSeparator_withSimpleSpaceSeparatedInput() {
        val input = """test1 test2   test3""".split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf("test1", "test2", "test3")
        assertArrayEquals(expected, actual)
    }

    @Test
    fun argumentsReSeparator_withBackslashedSpaceInput() {
        val input = """test1\ test2 test3""".split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf("test1 test2", "test3")
        assertArrayEquals(expected, actual)
    }

    @Test
    fun argumentsReSeparator_withQuotedInput() {
        val input = """test1 "test2 test2" test3""".split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf("test1", "test2 test2", "test3")
        assertArrayEquals(expected, actual)
    }

    @Test
    fun argumentsReSeparator_withComplexInput() {
        val input = """test1 "test2\" \\test2" "a"te"s"t3""".split(" ").toTypedArray()
        val actual = blankCommandExecutor.argumentsReSeparator(input)
        val expected = arrayOf("test1", "test2\" \\test2", "atest3")
        assertArrayEquals(expected, actual)
    }

    @Test
    fun onTabComplete() {
        val actual = argsCommandExecutor.onTabComplete(
            mock(CommandSender::class.java),
            mock(Command::class.java),
            "testing",
            """--n""".split(" ").toTypedArray()
        )?.toTypedArray()
        val expected = arrayOf("--name")
        assertArrayEquals(expected, actual)
    }

    @Test
    fun onCommand() {
        @CommandLine.Command(name = "test")
        open class TestCommand : ReactantCommand() {
            override fun run() {
            }
        }

        val command = mock(TestCommand::class.java)
        ReactantCommandExecutor(
            CommandLine.Model.CommandSpec.forAnnotatedObject(TestCommand::class.java),
            { command }
        ).onCommand(mock(CommandSender::class.java), mock(Command::class.java), "test", arrayOf(""))
        verify(command, times(1).description("run() should be called once")).run()
    }
}
