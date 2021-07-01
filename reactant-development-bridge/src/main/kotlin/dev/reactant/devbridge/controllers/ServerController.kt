package dev.reactant.devbridge.controllers

import dev.reactant.devbridge.Action
import dev.reactant.devbridge.plugin
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import kotlinx.coroutines.launch
import org.bukkit.Bukkit

internal class ServerController {
    val getAll: Action = {
        launch(plugin.dispatchers.main) {
            plugin.logger.info("Received reload request")
            Bukkit.reload()
        }
        call.respond(HttpStatusCode.OK)
    }
}
