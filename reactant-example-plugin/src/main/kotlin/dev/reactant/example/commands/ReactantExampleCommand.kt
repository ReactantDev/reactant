package dev.reactant.example.commands

import dev.reactant.command.ReactantCommand
import dev.reactant.example.plugin
import dev.reactant.modulardata.data.PersistentDataObjectAccessor
import dev.reactant.modulardata.module.DataModule
import dev.reactant.modulardata.module.modules
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import picocli.CommandLine

@CommandLine.Command(
    name = "reactant-example",
    aliases = ["reactantexample"],
    description = ["Example Command"]
)
class ReactantExampleCommand : ReactantCommand() {
    @CommandLine.Option(names = ["-d", "--damage"])
    var damage: Double = 0.0

    @CommandLine.Option(names = ["-g", "--add-gem"])
    var addGem: Boolean = false

    @CommandLine.Option(names = ["-i", "--info"])
    var info: Boolean = false

    override fun run() {
        val item = (sender as Player).inventory.itemInMainHand

        if (info) {
            item.modules.get(CustomSword.moduleKey)?.let {
                sender.sendMessage("damage: ${it.damage} + ${it.swordGem?.additionalDamage ?: 0}")
            }
        } else {
            item.modules
                .createIfNotExist(CustomSword.moduleKey, ::CustomSword)
                .mutateModule(CustomSword.moduleKey) { customSword ->
                    val originalDamage = customSword.damage
                    customSword.damage = damage
                    if (addGem) {
                        sender.sendMessage("Sword gem: ${customSword.swordGem}")
                        if (customSword.swordGem != null) {
                            customSword.swordGem!!.additionalDamage = 25.0
                            customSword.swordGem = customSword.swordGem
                            customSword.swordGem = customSword.swordGem
                        } else {
                            customSword.swordGem = SwordGem().apply {
                                additionalDamage = 50.0
                            }
                        }
                    }
                    sender.sendMessage("Change damage from $originalDamage to $damage")
                }
        }
    }
}

class CustomSword : PersistentDataObjectAccessor(plugin) {
    var damage: Double by staticProp()
    var swordGem: SwordGem? by staticProp()

    init {
        damage = 10.0
    }

    companion object {
        val moduleKey by lazy { DataModule(plugin, "testmodule", ::CustomSword) }
    }
}

class SwordGem : PersistentDataObjectAccessor(plugin) {
    var additionalDamage: Double by staticProp()
}
