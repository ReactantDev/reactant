package dev.reactant.modulardata.data

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure

private val primitiveDataTypeMap = listOf(
    PersistentDataType.BYTE,
    PersistentDataType.SHORT,
    PersistentDataType.INTEGER,
    PersistentDataType.LONG,
    PersistentDataType.FLOAT,
    PersistentDataType.DOUBLE,
    PersistentDataType.STRING,

    PersistentDataType.BYTE_ARRAY,
    PersistentDataType.INTEGER_ARRAY,
    PersistentDataType.LONG_ARRAY,

    PersistentDataType.TAG_CONTAINER_ARRAY,
    PersistentDataType.TAG_CONTAINER,
).map { it.primitiveType to it }

abstract class PersistentDataObjectAccessor(
    val namespace: Plugin,
    internal var container: PersistentDataContainer = DelayedPersistentDataContainer(),
) {
    internal var parentAccessor: PersistentDataObjectAccessor? = null
    internal var parentContainer: PersistentDataContainer? = null

    val staticDataProp = PersistentDataProperty<Any>(null)
    fun <T> staticProp(): PersistentDataProperty<T> {
        return staticDataProp as PersistentDataProperty<T>
    }

    inner class PersistentDataProperty<T>(private val customDataType: PersistentDataType<*, T>? = null) :
        ReadWriteProperty<Any, T> {

        private fun getDataTypeOrNull(type: KType): PersistentDataType<*, *>? {
            val clazz = type.jvmErasure

            return when (clazz) {
                Byte::class -> PersistentDataType.BYTE
                Short::class -> PersistentDataType.SHORT
                Int::class -> PersistentDataType.INTEGER
                Long::class -> PersistentDataType.LONG
                Float::class -> PersistentDataType.FLOAT
                Double::class -> PersistentDataType.DOUBLE
                String::class -> PersistentDataType.STRING

                Array::class -> when (type.arguments[0].type?.jvmErasure) {
                    Byte::class -> PersistentDataType.BYTE_ARRAY
                    Int::class -> PersistentDataType.INTEGER_ARRAY
                    Long::class -> PersistentDataType.LONG_ARRAY

                    Short::class -> PersistentDataType.INTEGER_ARRAY
                    else -> null
                }
                else -> null
            }
        }

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            val key = NamespacedKey.fromString(property.name.lowercase(), namespace)
                ?: throw IllegalArgumentException("Cannot convert ${property.name} to a valid key")
            val type = property.returnType
            val explicitDataType = customDataType ?: getDataTypeOrNull(type)
            val dataType = explicitDataType ?: PersistentDataType.TAG_CONTAINER

            if (type.isMarkedNullable && !container.has(key, dataType)) return null as T
            if (explicitDataType == null) {
                if (type.jvmErasure.isSubclassOf(PersistentDataObjectAccessor::class)) {
                    return (
                        type.jvmErasure.primaryConstructor
                            ?.takeIf { it.visibility == KVisibility.PUBLIC && it.parameters.isEmpty() }
                            ?: throw IllegalArgumentException("PersistentDataObjectAccessor must have a public primary parameterless constructor")
                        )
                        .let {
                            val objectContainer = container.get(key, PersistentDataType.TAG_CONTAINER)
                            if (objectContainer == null) return null as T
                            val objectAccessor = it.call() as PersistentDataObjectAccessor
                            objectAccessor.parentContainer = container
                            objectAccessor.container = objectContainer
                            objectAccessor as T
                        }
                } else {
                    throw IllegalArgumentException("${type.jvmErasure} is neither a PersistentDataObjectAccessor or a primitive type, you should specify the data type for it")
                }
            }
            return container.get(key, dataType) as T
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            val key = NamespacedKey.fromString(property.name.lowercase(), namespace)
                ?: throw IllegalArgumentException("Cannot convert ${property.name} to a valid key")
            val type = property.returnType
            val dataType = customDataType ?: getDataTypeOrNull(type)
            if (value == null) {
                container.remove(key)
                return
            }

            if (dataType == null) {
                if (value !is PersistentDataObjectAccessor) throw IllegalArgumentException("Cannot handle type ${value.let { it::class }}")
                when (value.container) {
                    is DelayedPersistentDataContainer -> {
                        container.set(
                            key,
                            DelayedPersistentDataContainer.dataType,
                            value.container as DelayedPersistentDataContainer
                        )
                    }
                    is PersistentDataObjectAccessor -> container.set(
                        key,
                        PersistentDataType.TAG_CONTAINER,
                        value.container
                    )
                }
            } else if (value is PersistentDataObjectAccessor) {
                container.set(key, dataType as PersistentDataType<*, PersistentDataContainer>, value.container)
            } else {
                container.set(key, dataType as PersistentDataType<*, T>, value)
            }
        }
    }

    inline fun <reified T> property(): PersistentDataProperty<T> {
        return PersistentDataProperty()
    }

    inline fun <reified T> property(customDataType: PersistentDataType<*, T>): PersistentDataProperty<T> {
        return PersistentDataProperty(customDataType)
    }
}

fun PersistentDataContainer.hasModule(key: NamespacedKey) {
    has(key, PersistentDataType.TAG_CONTAINER)
}

fun <T : PersistentDataObjectAccessor> PersistentDataContainer.getOrCreateModule(
    key: NamespacedKey,
    accessorCreator: () -> T,
) =
    accessorCreator().apply {
        container = get(key, PersistentDataType.TAG_CONTAINER) ?: adapterContext.newPersistentDataContainer()
    }
