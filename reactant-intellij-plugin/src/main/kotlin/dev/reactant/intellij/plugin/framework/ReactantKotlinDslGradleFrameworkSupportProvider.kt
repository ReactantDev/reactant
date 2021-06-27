package dev.reactant.intellij.plugin.framework

import com.intellij.framework.FrameworkTypeEx
import com.intellij.framework.addSupport.FrameworkSupportInModuleProvider
import com.intellij.openapi.externalSystem.model.project.ProjectId
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModifiableRootModel
import dev.reactant.intellij.plugin.ReactantPluginIcons
import org.jetbrains.plugins.gradle.frameworkSupport.BuildScriptDataBuilder
import org.jetbrains.plugins.gradle.frameworkSupport.KotlinDslGradleFrameworkSupportProvider
import javax.swing.Icon
import javax.swing.JCheckBox
import javax.swing.JComponent

class ReactantKotlinDslGradleFrameworkSupportProvider : KotlinDslGradleFrameworkSupportProvider() {
    override fun getFrameworkType(): FrameworkTypeEx {
        return object : FrameworkTypeEx(
            "dev.reactant.intellij.plugin.framework.ReactantPluginFramework"
        ) {
            override fun getPresentableName(): String = "Reactant Plugin"

            override fun getIcon(): Icon {
                return ReactantPluginIcons.reactantIcon
            }

            override fun createProvider(): FrameworkSupportInModuleProvider {
                return this@ReactantKotlinDslGradleFrameworkSupportProvider
            }
        }
    }

    override fun addSupport(
        projectId: ProjectId,
        module: Module,
        rootModel: ModifiableRootModel,
        modifiableModelsProvider: ModifiableModelsProvider,
        buildScriptData: BuildScriptDataBuilder
    ) {
        buildScriptData.addPropertyDefinition("group = \"dev.reactant\"")
        super.addSupport(projectId, module, rootModel, modifiableModelsProvider, buildScriptData)
    }

    override fun createComponent(): JComponent {
        return JCheckBox("Bungeecord support")
    }

    override fun isEnabledForModuleType(moduleType: ModuleType<*>): Boolean {
        return true
    }
}
