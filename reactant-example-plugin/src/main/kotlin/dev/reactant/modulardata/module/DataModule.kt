package dev.reactant.modulardata.module

import dev.reactant.modulardata.data.PersistentDataObjectAccessor
import org.bukkit.NamespacedKey
import org.bukkit.plugin.Plugin

data class DataModule<T : PersistentDataObjectAccessor>(
    val key: NamespacedKey,
    val objectAccessorCreator: () -> T,
) {
    constructor(plugin: Plugin, key: String, objectAccessorCreator: () -> T) : this(
        NamespacedKey.fromString(key, plugin)!!,
        objectAccessorCreator
    )
}
