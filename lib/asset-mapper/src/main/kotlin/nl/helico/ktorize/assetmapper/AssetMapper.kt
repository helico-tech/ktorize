package nl.helico.ktorize.assetmapper

import io.ktor.util.*
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

interface AssetMapper {

    interface Preload {
        fun preload(): List<String>
    }

    companion object {
        val Key = AttributeKey<AssetMapper>("AssetMapper")
    }

    val pathRegex: Regex

    fun matches(path: String): Boolean

    fun map(path: String): String
}

class DefaultAssetMapper(
    val baseUrl: String,
    val assetResolver: AssetResolver,
    private val cache: MutableMap<String, String> = ConcurrentHashMap()
) : AssetMapper, AssetMapper.Preload {

    override val pathRegex: Regex = createMappedAssetRegex(baseUrl)

    override fun matches(path: String): Boolean {
        return path.startsWith(baseUrl)
    }

    override fun preload(): List<String> {
        if (assetResolver is AssetResolver.ResolveAll) {
            val assets = assetResolver.resolveAll().map { "$baseUrl/${it.first}" }
            assets.forEach {
                map(it)
            }
            return assets
        }
        return emptyList()
    }

    override fun map(path: String): String {
        return cache.getOrPut(path) {
            val logicalPath = path.removePrefix(baseUrl).removePrefix("/")
            val asset = assetResolver.resolveOrNull(logicalPath) ?: return path

            val fileName = logicalPath.substringAfterLast('/')
            val directoryName = logicalPath.removeSuffix(fileName).removeSuffix("/")

            val parts = fileName.split(".")

            val extension = parts.last()

            val baseName = parts.dropLast(1).joinToString(".")

            val digest = asset.md5()

            listOf(
                baseUrl,
                directoryName,
                "$baseName.$digest.$extension"
            ).filterNot { it.isEmpty() }.joinToString("/")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun URL.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(this.readBytes())
        return digest.toHexString()
    }

}