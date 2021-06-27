package dev.reactant.command

import org.bukkit.command.CommandSender
import kotlin.reflect.KClass

abstract class ReactantCommand : Runnable {
    lateinit var sender: CommandSender

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T : ReactantCommand> getTabCompletingCommand(clazz: KClass<T>): T? {
            val value = tabCompletingCommandLine.get()
            return if (clazz.isInstance(value)) value as T? else null
        }

        inline fun <reified T : ReactantCommand> getTabCompletingCommand(): T? = getTabCompletingCommand(T::class)
    }
}
