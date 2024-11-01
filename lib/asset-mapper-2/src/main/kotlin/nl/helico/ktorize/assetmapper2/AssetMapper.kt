package nl.helico.ktorize.assetmapper2

import io.ktor.util.*
import nl.helico.ktorize.assetmapper2.handlers.AssetHandler
import nl.helico.ktorize.assetmapper2.handlers.DefaultHandler
import nl.helico.ktorize.assetmapper2.readers.AssetReader
import nl.helico.ktorize.assetmapper2.writers.AssetWriter
import java.nio.file.Path

typealias Context = Attributes

interface AssetMapper {
    val reader: AssetReader
    val writer: AssetWriter
    val digester: AssetDigester
    val pathTransformer: AssetPathTransformer

    fun map(path: Path, context: Context = Attributes()): Asset.Output
    fun write(output: Asset.Output)

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
    override val reader: AssetReader,
    override val writer: AssetWriter,
    val handlers: List<AssetHandler>,
    override val digester: AssetDigester = MD5AssetDigester,
    override val pathTransformer: AssetPathTransformer= AssetPathTransformer(),
): AssetMapper  {
    override fun map(path: Path, context: Context): Asset.Output {
        val input = reader.readAsset(path) ?: error("Invalid path $path")
        val handler = handlers.firstOrNull { it.accepts(input) } ?: DefaultHandler()
        val output = handler.handle(input, this, context)

        return output
    }

    override fun write(output: Asset.Output) {
        writer.writeAsset(output, includingDependencies = true)
    }
}