package nl.helico.ktorize.importmap

import io.ktor.util.*
import io.ktor.util.logging.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities.Private

@Serializable()
data class ImportMap(
    @SerialName("imports")
    val imports: Map<String, String>,
) {
    override fun toString(): String {
        return JsonSerializer.encodeToString(this)
    }

    companion object {
        val JsonSerializer = Json {
            prettyPrint = true
        }
    }
}

fun ImportMap(builder: ImportMapBuilder.() -> Unit): ImportMap {
    return ImportMapBuilderImpl().apply(builder).build()
}

interface ImportMapBuilder {
    companion object {
        val Key = AttributeKey<ImportMapBuilder>("ImportMapBuilder")
    }

    fun interface Provider {
        fun provide(builder: ImportMapBuilder)
    }

    fun addModuleSpecifier(moduleSpecifier: String, url: String)
    fun addProvider(provider: Provider)

    fun build(): ImportMap
}

class ImportMapBuilderImpl(
    private val silent: Boolean = false
) : ImportMapBuilder {
    private val imports = mutableMapOf<String, String>()
    private val providers = mutableListOf<ImportMapBuilder.Provider>()
    private val LOGGER = KtorSimpleLogger("ImportMapBuilderImpl")

    constructor() : this(silent = false)

    override fun addModuleSpecifier(moduleSpecifier: String, url: String) {
        if (!silent) LOGGER.trace("Adding module specifier: $moduleSpecifier -> $url")
        imports[moduleSpecifier] = url
    }

    override fun addProvider(provider: ImportMapBuilder.Provider) {
        if (!silent) LOGGER.trace("Adding provider: {}", provider)
        providers.add(provider)
    }

    override fun build(): ImportMap {
        val builder = ImportMapBuilderImpl()
        providers.forEach { provider ->
            provider.provide(builder)
        }
        imports.forEach { (moduleSpecifier, url) ->
            builder.addModuleSpecifier(moduleSpecifier, url)
        }
        return ImportMap(imports = builder.imports)
    }
}