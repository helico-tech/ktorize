package nl.helico.ktorize.assetmapper

import io.ktor.util.*
import kotlinx.io.Source
import nl.helico.ktorize.assetmapper.handlers.AssetHandler
import nl.helico.ktorize.assetmapper.handlers.DefaultHandler
import nl.helico.ktorize.assetmapper.readers.AssetReader
import java.nio.file.Path

typealias Context = Attributes

interface AssetMapper {
    fun read(path: Path): Asset.Input?

    fun digest(content: Source): String

    fun map(path: Path, context: Context = Attributes()): MapResult

    fun map(path: String, context: Context = Attributes()): MapResult {
        return map(Path.of(path).normalize(), context)
    }

    fun getTransformedPath(asset: Asset): Path

    fun getMappedAssetRegex(basePath: Path): Regex

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
            digester: AssetDigester = MD5AssetDigester,
            pathTransformer: AssetPathTransformer= AssetPathTransformer(),
        ): AssetMapper = AssetMapperImpl(reader, handlers, digester, pathTransformer)
    }
}

class AssetMapperImpl(
    private val reader: AssetReader,
    private val handlers: List<AssetHandler>,
    private val digester: AssetDigester = MD5AssetDigester,
    private val pathTransformer: AssetPathTransformer= AssetPathTransformer(),
): AssetMapper  {

    override fun read(path: Path): Asset.Input? {
        return reader.readAsset(path, digester)
    }

    override fun digest(content: Source): String {
        return digester.digest(content)
    }

    override fun map(path: Path, context: Context): AssetMapper.MapResult {
        val input = reader.readAsset(path, digester) ?: return AssetMapper.MapResult.NotFound
        val result = kotlin.runCatching {
            val handler = handlers.firstOrNull { it.accepts(input) } ?: DefaultHandler()
            val output = handler.handle(input, this, context)
            AssetMapper.MapResult.Mapped(output)
        }

        return when {
            result.isFailure -> AssetMapper.MapResult.Error(path, result.exceptionOrNull()!!)
            else -> result.getOrThrow()
        }
    }

    override fun getTransformedPath(asset: Asset) = when (asset) {
        is Asset.Input -> pathTransformer.transform(asset.path, asset.digest)
        is Asset.Output -> asset.path
    }

    override fun getMappedAssetRegex(basePath: Path): Regex {
        return pathTransformer.createMappedAssetRegex(basePath)
    }
}