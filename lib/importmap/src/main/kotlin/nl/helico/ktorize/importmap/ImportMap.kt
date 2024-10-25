package nl.helico.ktorize.importmap

import io.ktor.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable()
data class ImportMap(
    @SerialName("imports")
    val imports: Map<String, String>,
)

fun ImportMap(builder: ImportMapBuilder.() -> Unit): ImportMap {
    return ImportMapBuilderImpl().apply(builder).build()
}

interface ImportMapBuilder {
    companion object {
        val Key = AttributeKey<ImportMapBuilder>("ImportMapBuilder")
    }

    fun addModuleSpecifier(moduleSpecifier: String, url: String)
    fun build(): ImportMap
}

class ImportMapBuilderImpl : ImportMapBuilder {
    private val imports = mutableMapOf<String, String>()

    override fun addModuleSpecifier(moduleSpecifier: String, url: String) {
        imports[moduleSpecifier] = url
    }

    override fun build(): ImportMap = ImportMap(imports)
}