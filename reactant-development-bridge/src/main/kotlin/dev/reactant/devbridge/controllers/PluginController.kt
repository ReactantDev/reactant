package dev.reactant.devbridge.controllers

import dev.reactant.devbridge.Action
import dev.reactant.devbridge.plugin
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import kotlinx.serialization.Serializable
import org.bukkit.Bukkit
import java.io.File

internal class PluginController {

    @Serializable
    data class PluginInfo(val name: String, val path: String)

    val getAll: Action = {
        call.respond(
            Bukkit.getPluginManager().plugins.map {
                PluginInfo(it.name, it.javaClass.protectionDomain.codeSource.location.path)
            }
        )
    }

    val upload: Action = {
        call.receiveMultipart().forEachPart {
            if (it is PartData.FileItem) {
                plugin.logger.info("Received plugin upload request - ${it.name}")
                var targetOutputFile = File("plugins/${it.name}.jar")
                if (targetOutputFile.exists()) {
                    File("plugins/update").mkdir()
                    targetOutputFile = File("plugins/update/${it.name}.jar")
                }
                it.streamProvider().use { input ->
                    targetOutputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            it.dispose()
        }
        call.respond(HttpStatusCode.OK)
    }
}
