package dev.reactant.devbridge

import dev.reactant.devbridge.controllers.PluginController
import dev.reactant.devbridge.controllers.ServerController
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal val apiModules = DI.Module(name = "Reactant DevBridge API") {
    bind { singleton { PluginController() } }
    bind { singleton { ServerController() } }
    bind { singleton { Routes(di) } }
}
