package dev.reactant.intellij.plugin.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.openapi.module.ModuleType

class ReactantPluginModuleBuilder : ModuleBuilder() {
    override fun getModuleType(): ModuleType<*> {
        return ReactantPluginModuleType.getInstance()
    }
}
