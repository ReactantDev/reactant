package dev.reactant.devbridge

import dev.reactant.coroutines.ReactantDispatchers
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.routing.routing
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.bukkit.plugin.java.JavaPlugin
import org.kodein.di.DI
import org.kodein.di.newInstance

class ReactantDevelopmentBridgePlugin : JavaPlugin() {
    internal val dispatchers by lazy { ReactantDispatchers(instance) }

    private val di = DI { import(apiModules) }

    private val routes by di.newInstance { Routes(di) }

    private val apiServer = embeddedServer(
        Netty,
        port = 38230,
        host = "127.0.0.1",
    ) {
        install(ContentNegotiation) {
            json()
        }
        routing(routes.route)
    }

    override fun onEnable() {
        instance = this

        apiServer.start()
        server.onlinePlayers.forEach { it.sendMessage("Dev bridge started") }
    }

    override fun onDisable() {
        server.onlinePlayers.forEach { it.sendMessage("Dev bridge stopping") }
        apiServer.stop(0, 0)
    }

    companion object {
        lateinit var instance: ReactantDevelopmentBridgePlugin
    }
}

internal val plugin get() = ReactantDevelopmentBridgePlugin.instance
