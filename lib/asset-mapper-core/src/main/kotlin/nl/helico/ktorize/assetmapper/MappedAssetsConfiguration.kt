package nl.helico.ktorize.assetmapper

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable()
data class MappedAssetsConfiguration(
    val basePackage: String,
    val root: AssetTree.Folder
) {
    fun toJSON(): String {
        return Json.encodeToString(this)
    }

    companion object {

        const val DEFAULT_FILE_NAME = "mapped-assets.configuration.json"

        private val Json = Json {
            prettyPrint = true
        }

        fun fromJSON(json: String): MappedAssetsConfiguration {
            return Json.decodeFromString(json)
        }
    }
}

@Serializable
sealed interface AssetTree {

    @Serializable
    @SerialName("folder")
    data class Folder(val path: String, val items: List<AssetTree>) : AssetTree

    @Serializable
    @SerialName("file")
    data class File(val path: String) : AssetTree
}