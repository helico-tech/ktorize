package nl.helico.ktorize.di

import io.ktor.util.*
import org.kodein.di.DI

interface DIModuleRegistry {

    val modules: List<DI.Module>

    fun register(module: DI.Module)

    fun register(name: String, builder: DI.Builder.() -> Unit) {
        val module = DI.Module(name) {
            builder(this)
        }

        register(module)
    }

    companion object {
        val Key = AttributeKey<DIModuleRegistry>("DIModuleRegistry")
    }
}

fun DIModuleRegistry(): DIModuleRegistry = DIModuleRegistryImpl()

class DIModuleRegistryImpl : DIModuleRegistry {

    override val modules = mutableListOf<DI.Module>()

    override fun register(module: DI.Module) {
        modules.add(module)
    }

    fun build(): DI {
        return DI {
            modules.forEach { import(it) }
        }
    }
}