package nl.helico.ktorize.assetmapper2

import io.ktor.util.*
import nl.helico.ktorize.assetmapper2.handlers.AssetHandler
import nl.helico.ktorize.assetmapper2.handlers.DefaultHandler
import nl.helico.ktorize.assetmapper2.readers.AssetReader
import nl.helico.ktorize.assetmapper2.writers.AssetWriter
import java.nio.file.Path

typealias Context = Attributes

interface AssetMapper {
    fun read(path: Path): Asset.Input?

    fun digest(lines: List<String>): String

    fun map(path: Path, context: Context = Attributes()): Asset.Output

    fun write(output: Asset.Output)

    fun getTransformedPath(asset: Asset): Path

    companion object {
        operator fun invoke(
            reader: AssetReader,
            writer: AssetWriter,
            handlers: List<AssetHandler>,
            digester: AssetDigester = MD5AssetDigester,
            pathTransformer: AssetPathTransformer= AssetPathTransformer(),
        ): AssetMapper = AssetMapperImpl(reader, writer, handlers, digester, pathTransformer)
    }
}

class AssetMapperImpl(
    private val reader: AssetReader,
    private val writer: AssetWriter,
    private val handlers: List<AssetHandler>,
    private val digester: AssetDigester = MD5AssetDigester,
    private val pathTransformer: AssetPathTransformer= AssetPathTransformer(),
): AssetMapper  {
    override fun read(path: Path): Asset.Input? {
        return reader.readAsset(path, digester)
    }

    override fun digest(lines: List<String>): String {
        return digester.digest(lines)
    }

    override fun map(path: Path, context: Context): Asset.Output {
        val input = reader.readAsset(path, digester) ?: error("Invalid path $path")
        val handler = handlers.firstOrNull { it.accepts(input) } ?: DefaultHandler()
        val output = handler.handle(input, this, context)

        return output
    }

    override fun getTransformedPath(asset: Asset) = when (asset) {
        is Asset.Input -> pathTransformer.transform(asset.path, asset.digest)
        is Asset.Output -> asset.path
    }

    override fun write(output: Asset.Output) {
        writer.writeAsset(output, includingDependencies = true)
    }
}