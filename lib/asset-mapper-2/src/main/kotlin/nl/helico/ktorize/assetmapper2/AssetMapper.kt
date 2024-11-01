package nl.helico.ktorize.assetmapper2

import java.nio.file.Path

class AssetMapper(
    private val assetReader: AssetReader,
    private val assetWriter: AssetWriter,
    private val assetHandlers: List<AssetHandler>,
    private val assetDigester: AssetDigester = MD5AssetDigester,
    private val assetPathTransformer: AssetPathTransformer= AssetPathTransformer(),
)  {
    fun map(path: Path): Asset.Output {
        val data = assetReader.read(path)?.readLines() ?: error("Invalid path $path")
        val input = Asset.Input(path, data)

        val handler = assetHandlers.firstOrNull { it.accepts(input) } ?: DefaultHandler()

        val output = handler.handle(input, assetDigester, assetPathTransformer)

        return output
    }

    fun write(output: Asset.Output) {
        val writer = assetWriter.writer(output.path) ?: error("Invalid path ${output.path}")
        writer.use {
            output.data.forEach { line ->
                it.appendLine(line)
            }
        }

        output.dependencies.forEach { write(it) }
    }
}