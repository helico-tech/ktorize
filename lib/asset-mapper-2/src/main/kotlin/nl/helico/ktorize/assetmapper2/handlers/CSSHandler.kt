package nl.helico.ktorize.assetmapper2.handlers

import io.ktor.http.*
import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import java.nio.file.Path
import kotlin.io.path.Path

class CSSHandler : BaseHandler() {

    private val importUrlRegex = """@import\s+url\((['"]?)(.*?)\1\)\s*;""".toRegex()
    private val importDirectRegex = """@import\s+(['"])(.*?)\1\s*;""".toRegex()

    override fun accepts(input: Asset.Input): Boolean {
        return input.contentType.match(ContentType.Text.CSS)
    }

    override fun handle(input: Asset.Input, mapper: AssetMapper): Asset.Output {
        val handledAssetPaths = mutableSetOf(input.path)
        val dependencies = mutableListOf<Asset.Output>()
        val transformedLines = mutableListOf<String>()

        input.lines.forEach { line ->
            val importUrlMatch = importUrlRegex.find(line)
            val importDirectMatch = importDirectRegex.find(line)

            val url = importUrlMatch?.groupValues?.get(2) ?: importDirectMatch?.groupValues?.get(2)
            if (url == null) {
                transformedLines.add(line)
                return@forEach
            }

            val relativePath = getPathFromUrl(url)
            if (relativePath == null) {
                transformedLines.add(line)
                return@forEach
            }

            val path = (input.path.parent ?: Path(".")).resolve(relativePath).normalize()

            if (handledAssetPaths.contains(path)) error("Circular dependency detected: $path")
            handledAssetPaths.add(path)

            val asset = mapper.map(path)
            val digest = mapper.digester.digest(asset.lines)
            val transformedPath = mapper.pathTransformer.transform(asset.path, digest)
            val transformedLine = line.replace(relativePath.fileName.toString(), transformedPath.fileName.toString())

            transformedLines.add(transformedLine)
            dependencies.add(asset)
        }


        val baseOutput = super.handle(input, mapper)
        return Asset.Output(baseOutput.path, transformedLines, dependencies)
    }

    private fun getPathFromUrl(url: String): Path? {
        if (url.startsWith("http")) return null
        return Path(url)
    }
}