package nl.helico.ktorize.assetmapper

import java.io.File
import java.net.URL

interface AssetResolver {

    interface ResolveAll : AssetResolver {
        fun resolveAll(): List<Pair<String, URL>>
    }

    fun resolveOrNull(path: String): URL?
}

class ResourceAssetResolver(
    val basePackage: String,
    val classLoader: ClassLoader
) : AssetResolver, AssetResolver.ResolveAll {

    override fun resolveOrNull(path: String): URL? {
        val resourcePath = "$basePackage/$path"
        return classLoader.getResource(resourcePath)
    }

    override fun resolveAll(): List<Pair<String, URL>> {
        return resolveRecusively("")
    }

    private fun resolveRecusively(path: String): List<Pair<String, URL>> {
        val resourcePath = "$basePackage/$path"
        val resources = classLoader.getResources(resourcePath).toList()
        return resources.flatMap { resource ->
            val file = File(resource.file)
            if (file.isDirectory) {
                file.listFiles()!!.flatMap { resolveRecusively("$path/${it.name}") }
            } else {
                listOf(path.removePrefix("/") to resource)
            }
        }
    }
}