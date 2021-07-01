package dev.reactant.devbridge

import dev.reactant.devbridge.controllers.PluginController
import dev.reactant.devbridge.controllers.ServerController
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class Routes(override val di: DI) : DIAware {
    private val pluginController: PluginController by instance()
    private val serverController: ServerController by instance()

    val route: Routing.() -> Unit = {
        route("/plugins") {
            get(pluginController.getAll)
            post(pluginController.upload)
        }
        route("/reload") {
            post(serverController.getAll)
        }
    }
}
