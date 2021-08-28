package dev.reactant.modulardata.module

import org.bukkit.persistence.PersistentDataHolder

val PersistentDataHolder.modules get() = DataModulesAccessor({ this.persistentDataContainer }, {})
