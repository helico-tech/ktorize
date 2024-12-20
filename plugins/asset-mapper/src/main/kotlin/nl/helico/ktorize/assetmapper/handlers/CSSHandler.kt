package nl.helico.ktorize.assetmapper.handlers

import io.ktor.util.logging.*
import io.ktor.utils.io.core.*
import nl.helico.ktorize.assetmapper.Asset
import nl.helico.ktorize.assetmapper.AssetMapper
import nl.helico.ktorize.assetmapper.Context
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.extension

class CSSHandler(
    val strict: Boolean = true
) : BaseHandler() {

    private val urlRegex = """url\((['"]?)(.*?)\1\)\s*;""".toRegex()
    private val importDirectRegex = """@import\s+(['"])(.*?)\1\s*;""".toRegex()
    private val logger = KtorSimpleLogger("CSSHandler")

    override fun accepts(input: Asset.Input): Boolean {
        return input.path.extension == "css"
    }

    override fun handle(input: Asset.Input, mapper: AssetMapper, context: Context): Asset.Output {
        val dependencyTracker = context.computeIfAbsent(DependencyTracker.Key) { DependencyTracker() }

        val dependencies = mutableListOf<Asset.Output>()
        val transformedLines = mutableListOf<String>()

        val lines = input.source.toString(charset = Charsets.UTF_8).lines()

        lines.forEach { line ->
            val url = listOf(
                urlRegex,
                importDirectRegex
            ).firstNotNullOfOrNull { regex -> regex.find(line)?.groupValues?.get(2) }
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

            when (val assetResult = mapper.map(path, context)) {
                is AssetMapper.MapResult.NotFound -> when {
                    strict -> error("Asset not found: $path")
                    else -> {
                        logger.warn("Asset not found: $path")
                        transformedLines.add(line)
                    }
                }

                is AssetMapper.MapResult.Error -> throw assetResult.error
                is AssetMapper.MapResult.Mapped -> {
                    transformedLines.add(
                        line.replace(
                            relativePath.fileName.toString(),
                            assetResult.output.path.fileName.toString()
                        )
                    )
                    dependencies.add(assetResult.output)
                }
            }
        }


        val content = transformedLines.joinToString(System.lineSeparator())
        val source = content.toByteArray()

        return Asset.Output(
            input = input,
            source = source,
            digest = mapper.digest(source),
            dependencies = dependencies
        )
    }


    private fun getPathFromUrl(url: String): Path? {
        if (url.startsWith("http")) return null
        return Path(url)
    }

}