package nl.helico.ktorize.assetmapper2.handlers

import io.ktor.http.*
import nl.helico.ktorize.assetmapper2.Asset
import nl.helico.ktorize.assetmapper2.AssetMapper
import nl.helico.ktorize.assetmapper2.Context
import java.nio.file.Path
import kotlin.io.path.Path

class CSSHandler : BaseHandler() {

    private val urlRegex = """url\((['"]?)(.*?)\1\)\s*;""".toRegex()
    private val importDirectRegex = """@import\s+(['"])(.*?)\1\s*;""".toRegex()

    override fun accepts(input: Asset.Input): Boolean {
        return input.contentType.match(ContentType.Text.CSS)
    }

    override fun handle(input: Asset.Input, mapper: AssetMapper, context: Context): Asset.Output {
        val dependencyTracker = context.computeIfAbsent(DependencyTracker.Key) { DependencyTracker() }

        val dependencies = mutableListOf<Asset.Output>()
        val transformedLines = mutableListOf<String>()

        input.lines.forEach { line ->
            val url = listOf(urlRegex, importDirectRegex).firstNotNullOfOrNull { regex -> regex.find(line)?.groupValues?.get(2) }
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
            if (!dependencyTracker.addDependency(input.path, path)) error("Circular dependency detected")

            val asset = mapper.map(path, context)
            val transformedPath = mapper.getTransformedPath(asset)
            val transformedLine = line.replace(relativePath.fileName.toString(), transformedPath.fileName.toString())

            transformedLines.add(transformedLine)
            dependencies.add(asset)
        }


        return super.handle(
            input = input.copy(lines = transformedLines),
            mapper = mapper,
            context = context).copy(
            lines = transformedLines,
            dependencies = dependencies
        )
    }

    private fun getPathFromUrl(url: String): Path? {
        if (url.startsWith("http")) return null
        return Path(url)
    }
}