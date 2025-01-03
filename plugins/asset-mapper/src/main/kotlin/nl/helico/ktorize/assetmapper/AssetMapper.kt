package nl.helico.ktorize.assetmapper

import io.ktor.util.*
import nl.helico.ktorize.assetmapper.handlers.AssetHandler
import nl.helico.ktorize.assetmapper.readers.AssetReader
import nl.helico.ktorize.assetmapper.handlers.DefaultHandler
import java.nio.file.Path

typealias Context = Attributes

interface AssetMapper {
    fun read(path: Path): Asset.Input?

    fun digest(content: ByteArray): String

    fun map(path: Path, context: Context = Attributes()): MapResult

    fun map(path: String, context: Context = Attributes()): MapResult {
        return map(Path.of(path).normalize(), context)
    }

    sealed interface MapResult {
        data object NotFound: MapResult
        data class Mapped(val output: Asset.Output): MapResult
        data class Error(val path: Path, val error: Throwable): MapResult
    }

    companion object {
        val Key = AttributeKey<AssetMapper>("AssetMapper")

        operator fun invoke(
            reader: AssetReader,
            handlers: List<AssetHandler>,
            digester: AssetDigester = Base64AssetDigester,
        ): AssetMapper = AssetMapperImpl(reader, handlers, digester)
    }
}

class AssetMapperImpl(
    private val reader: AssetReader,
    private val handlers: List<AssetHandler>,
    private val digester: AssetDigester,
): AssetMapper  {

    private val mapCache = mutableMapOf<Path, AssetMapper.MapResult>()

    override fun read(path: Path): Asset.Input? {
        return reader.readAsset(path, digester)
    }

    override fun digest(content: ByteArray): String {
        return digester.digest(content)
    }

    override fun map(path: Path, context: Context): AssetMapper.MapResult {
        return mapCache.getOrPut(path) {
            val input = reader.readAsset(path, digester) ?: return AssetMapper.MapResult.NotFound
            val result = kotlin.runCatching {
                val handler = handlers.firstOrNull { it.accepts(input) } ?: DefaultHandler()
                val output = handler.handle(input, this, context)
                AssetMapper.MapResult.Mapped(output)
            }

            return when {
                result.isFailure -> AssetMapper.MapResult.Error(path, result.exceptionOrNull()!!)
                else -> result.getOrThrow().also(::cacheResultRecursive)
            }
        }
    }

    private fun cacheResultRecursive(result: AssetMapper.MapResult.Mapped) {
        mapCache[result.output.path] = result
        result.output.dependencies.map { AssetMapper.MapResult.Mapped(it) }.forEach(::cacheResultRecursive)
    }
}