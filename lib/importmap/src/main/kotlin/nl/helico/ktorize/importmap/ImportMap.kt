package nl.helico.ktorize.importmap

import io.ktor.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable()
data class ImportMap(
    @SerialName("imports")
    val imports: Map<String, String>,
) {
    override fun toString(): String {
        return Json.encodeToString(this)
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

class ImportMapBuilderImpl : ImportMapBuilder {
    private val imports = mutableMapOf<String, String>()
    private val providers = mutableListOf<ImportMapBuilder.Provider>()

    override fun addModuleSpecifier(moduleSpecifier: String, url: String) {
        imports[moduleSpecifier] = url
    }

    override fun addProvider(provider: ImportMapBuilder.Provider) {
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