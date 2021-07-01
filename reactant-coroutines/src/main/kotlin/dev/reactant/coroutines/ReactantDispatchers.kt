package dev.reactant.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import kotlin.coroutines.CoroutineContext

class ReactantDispatchers(val plugin: JavaPlugin) {
    val main: CoroutineDispatcher = object : CoroutineDispatcher() {
        override fun dispatch(context: CoroutineContext, block: Runnable) {
            if (plugin.server.isPrimaryThread) block.run()
            else Bukkit.getScheduler().runTask(plugin, block)
        }
    }
}
