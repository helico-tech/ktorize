package nl.helico.ktorize.assetmapper2

import java.nio.file.Path

class AssetMapper(
    private val assetReader: AssetReader,
    private val assetWriter: AssetWriter,
    private val assetHandlers: List<AssetHandler>
)  {
    fun map(path: Path) {
        val data = assetReader.read(path)?.readLines() ?: error("Invalid path $path")
        val input = Asset.Input(path, data)

        val handler = assetHandlers.firstOrNull { it.accepts(input) } ?: DefaultHandler()
        val output = handler.handle(input)

        val writer = assetWriter.writer(output.path) ?: error("Invalid path ${output.path}")
        writer.use {
            output.data.forEach { line -> writer.appendLine(line) }
        }
    }
}