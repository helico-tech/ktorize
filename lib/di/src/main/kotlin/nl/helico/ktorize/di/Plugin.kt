package nl.helico.ktorize.di

import io.ktor.server.application.*
import io.ktor.util.*
import org.kodein.di.DI as KodeinDI
import nl.helico.ktorize.di.DIModuleRegistry as ModuleRegistry
import nl.helico.ktorize.di.DIModuleRegistry.Companion as ModuleRegistryCompanion

internal val name = "DI"

val DIKey get() = AttributeKey<KodeinDI>(name)

val DIPlugin = createApplicationPlugin(name) {

    val modules = application.ModuleRegistry.modules

    val module = KodeinDI {
        modules.forEach { import(it) }
    }

    application.attributes.put(DIKey, module)
}

val Application.DIModuleRegistry: ModuleRegistry get() = attributes.computeIfAbsent(ModuleRegistryCompanion.Key) { DIModuleRegistryImpl() }

val Application.DI: KodeinDI get() = attributes[DIKey]