package dev.reactant.modulardata.data

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

internal class DelayedPersistentDataContainer : PersistentDataContainer {
    data class DataValue<Z : Any>(
        val persistentDataType: PersistentDataType<Any, Z>,
        val value: Z,
    )

    private val keyValues: HashMap<NamespacedKey, DataValue<out Any>> = hashMapOf()

    override fun <T : Any, Z : Any> set(key: NamespacedKey, type: PersistentDataType<T, Z>, value: Z) {
        keyValues[key] = DataValue(type as PersistentDataType<Any, Z>, value)
    }

    override fun <T : Any, Z : Any> has(key: NamespacedKey, type: PersistentDataType<T, Z>) = keyValues.containsKey(key)

    override fun <T : Any, Z : Any> get(key: NamespacedKey, type: PersistentDataType<T, Z>): Z {
        val value = keyValues[key]!!.value
        return (value as? Z)
            ?: throw IllegalArgumentException("The class of the value is ${value::class}, but you are trying to get as ${type.complexType}")
    }

    override fun <T : Any, Z : Any> getOrDefault(
        key: NamespacedKey,
        type: PersistentDataType<T, Z>,
        defaultValue: Z,
    ): Z = if (has(key, type)) get(key, type) else defaultValue

    override fun getKeys(): Set<NamespacedKey> = keyValues.keys

    override fun remove(key: NamespacedKey) {
        keyValues.remove(key)
    }

    override fun isEmpty(): Boolean = keyValues.isEmpty()

    override fun getAdapterContext(): PersistentDataAdapterContext =
        throw UnsupportedOperationException("Delayed persisten data container do not have an adapter context")

    private fun replay(target: PersistentDataContainer) {
        println(keyValues)
        keyValues.forEach { (key, typeValue) ->
            println("$key $typeValue")
            target.set(key, typeValue.persistentDataType as PersistentDataType<Any, Any>, typeValue.value)
        }
    }

    companion object {
        val dataType = object : PersistentDataType<PersistentDataContainer, DelayedPersistentDataContainer> {
            override fun getPrimitiveType(): Class<PersistentDataContainer> = PersistentDataContainer::class.java

            override fun getComplexType(): Class<DelayedPersistentDataContainer> =
                DelayedPersistentDataContainer::class.java

            override fun toPrimitive(
                complex: DelayedPersistentDataContainer,
                context: PersistentDataAdapterContext,
            ): PersistentDataContainer {
                println("replying")
                return context.newPersistentDataContainer().apply { complex.replay(this) }
            }

            override fun fromPrimitive(
                primitive: PersistentDataContainer,
                context: PersistentDataAdapterContext,
            ): DelayedPersistentDataContainer =
                throw UnsupportedOperationException("DelayedPersistentDataContainer is a temporary container, should not be created from primitive")
        }
    }
}
