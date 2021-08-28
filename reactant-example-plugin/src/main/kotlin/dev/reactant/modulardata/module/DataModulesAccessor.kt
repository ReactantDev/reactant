package dev.reactant.modulardata.module

import dev.reactant.modulardata.data.DelayedPersistentDataContainer
import dev.reactant.modulardata.data.PersistentDataObjectAccessor
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

class DataModulesAccessor(
    val dataContainerGetter: () -> PersistentDataContainer,
    val commitChanges: () -> Any,
) {

    val parentContainer get() = dataContainerGetter()

    /**
     * Check if a module exist
     */
    fun <T : PersistentDataObjectAccessor> has(module: DataModule<T>): Boolean {
        return parentContainer.has(module.key, PersistentDataType.TAG_CONTAINER)
    }

    /**
     * Get a module, null if not exist
     */
    fun <T : PersistentDataObjectAccessor> get(module: DataModule<T>): T? {
        return parentContainer.get(module.key, PersistentDataType.TAG_CONTAINER)?.let {
            module.objectAccessorCreator().apply {
                container = it
                parentContainer = this@DataModulesAccessor.parentContainer
            }
        }
    }

    fun <T : PersistentDataObjectAccessor> createIfNotExist(
        module: DataModule<T>,
        dataCreator: () -> T,
    ): DataModulesAccessor {
        if (!has(module)) {
            this.upsertModule(module, dataCreator())
            this.commitChanges()
        }
        return this
    }

    /**
     * Get a module, create and insert if not exist
     */
    fun <T : PersistentDataObjectAccessor> getOrPut(module: DataModule<T>, dataCreator: () -> T): T {
        this.createIfNotExist(module, dataCreator)
        return this.get(module)!!
    }

    /**
     * Get a module, return default if not exist
     */
    fun <T : PersistentDataObjectAccessor> getOrDefault(module: DataModule<T>, moduleFactory: () -> T): T {
        return if (has(module)) this.get(module)!! else moduleFactory()
    }

    /**
     * Update or insert a module in the holder
     */
    fun <T : PersistentDataObjectAccessor> upsertModule(module: DataModule<T>, data: T): DataModulesAccessor {
        val original = get(module)
        if (original?.container != null && original.container == data.container) {
            return this
        }
        if (data.parentContainer == null) data.parentContainer = this@DataModulesAccessor.parentContainer
        if (data.parentContainer != parentContainer) throw IllegalArgumentException("The data is likely belongs to other container")
        if (data.container is DelayedPersistentDataContainer) {
            this.dataContainerGetter().set(
                module.key,
                DelayedPersistentDataContainer.dataType,
                data.container as DelayedPersistentDataContainer
            )
        } else {
            this.dataContainerGetter().set(
                module.key,
                PersistentDataType.TAG_CONTAINER,
                data.container
            )
        }
        this.commitChanges()
        return this
    }

    /**
     * Mutate a module in the holder if module exist
     *
     * Keep the mutation function atomic, nested upsert/mutation on same module will cause overwrite issues
     * Remind that you should not do any async changes inside the mutation, those changes will not be committed
     */
    fun <T : PersistentDataObjectAccessor> mutateModule(
        module: DataModule<T>,
        mutation: (T) -> Any,
    ): DataModulesAccessor {
        this.get(module)?.also {
            mutation(it)
            this.upsertModule(module, it)
        }
        this.commitChanges()
        return this
    }

    fun <T : PersistentDataObjectAccessor> removeModule(module: DataModule<T>): DataModulesAccessor {
        this.dataContainerGetter().remove(module.key)
        this.commitChanges()
        return this
    }
}
