package dev.reactant.modulardata

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import java.lang.reflect.Type
import kotlin.reflect.KClass


class DataModulesAccessor(
    val dataContainerGetter: () -> PersistentDataContainer,
    val commitChanges: () -> Any
) {

    val json = Json

    /**
     * Check if a module exist
     */
    inline fun <reified T: Any> has(module: DataModule<T>): Boolean {
        return this.dataContainerGetter().has(module.key, PersistentDataType.BYTE_ARRAY)
    }

    /**
     * Get a module, null if not exist
     */
    inline fun <reified T : Any> get(module: DataModule<T>): T? {
        return this.dataContainerGetter().get(module.key, PersistentDataType.BYTE_ARRAY)
            ?.let { json.decodeFromString<T>(String(it, Charsets.UTF_8)) }
    }

    inline fun <reified T : Any> createIfNotExist(moduleFactory: () -> T): DataModulesAccessor {
        val key = getModuleKey(T::class)
        if (!this.dataContainerGetter().has(key, PersistentDataType.STRING)) {
            this.upsertModule(moduleFactory())
            this.commitChanges();
        }
        return this
    }

    /**
     * Get a module, create and insert if not exist
     */
    inline fun <reified T : Any> getOrPut(module: DataModule<T>,moduleFactory: () -> T): T {
        this.createIfNotExist(moduleFactory)
        return this.get<T>()!!
    }

    /**
     * Get a module, return default if not exist
     */
    inline fun <reified T : Any> getOrDefault(moduleFactory: () -> T): T {
        val key = getModuleKey(T::class)
        return if (this.dataContainerGetter().has(key, PersistentDataType.STRING)) moduleFactory() else this.get<T>()!!
    }

    /**
     * Update or insert a module in the holder
     */
    inline fun <reified T : Any> upsertModule(module: T): DataModulesAccessor {
        this.dataContainerGetter().set(
            getModuleKey(T::class),
            PersistentDataType.STRING,
            `access$parser`.encode(module, false).blockingGet()
        )
        this.commitChanges();
        return this
    }

    /**
     * Mutate a module in the holder if module exist
     *
     * Keep the mutation function atomic, nested upsert/mutation on same module will cause overwrite issues
     * Remind that you should not do any async changes inside the mutation, those changes will not be committed
     */
    inline fun <reified T : Any> mutateModule(mutation: (T) -> Any): DataModulesAccessor {
        this.get<T>()?.also {
            mutation(it)
            this.upsertModule(it)
        }
        this.commitChanges();
        return this
    }

    inline fun <reified T : Any> removeModule(): DataModulesAccessor {
        this.dataContainerGetter().remove(getModuleKey(T::class))
        this.commitChanges();
        return this;
    }
}
