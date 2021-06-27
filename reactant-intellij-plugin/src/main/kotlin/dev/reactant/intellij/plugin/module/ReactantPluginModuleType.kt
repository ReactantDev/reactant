package dev.reactant.intellij.plugin.module

import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.module.ModuleTypeManager
import dev.reactant.intellij.plugin.ReactantPluginIcons
import javax.swing.Icon

internal const val reactantPluginModuleId = "dev.reactant.intellij.plugin.module"
class ReactantPluginModuleType : ModuleType<ReactantPluginModuleBuilder>(reactantPluginModuleId) {
    companion object {
        fun getInstance(): ReactantPluginModuleType {
            return ModuleTypeManager.getInstance().findByID(reactantPluginModuleId) as ReactantPluginModuleType
        }
    }

    override fun createModuleBuilder(): ReactantPluginModuleBuilder {
        return ReactantPluginModuleBuilder()
    }

    override fun getName(): String {
        return "Reactant"
    }

    override fun getDescription(): String {
        return "Reactant based plugin"
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return ReactantPluginIcons.reactantIcon
    }
}
