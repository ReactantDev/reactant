package dev.reactant.modulardata.module

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

private fun ItemStack.getOrCreateItemMeta(): ItemMeta {
    if (!this.hasItemMeta()) {
        if (this.type === Material.AIR) throw UnsupportedOperationException("Air do not have item meta")
        this.itemMeta = Bukkit.getItemFactory().getItemMeta(this.type)
    }
    return this.itemMeta!!
}

val ItemStack.modules: DataModulesAccessor
    get() {
        val itemMeta = this.getOrCreateItemMeta()
        return DataModulesAccessor(
            { itemMeta.persistentDataContainer }, { this.setItemMeta(itemMeta) }
        )
    }
