package dev.reactant.modulardata

import org.bukkit.NamespacedKey
import kotlin.reflect.KClass

data class DataModule<T : Any>(
    val key: NamespacedKey,
    val clazz: KClass<T>,
)
