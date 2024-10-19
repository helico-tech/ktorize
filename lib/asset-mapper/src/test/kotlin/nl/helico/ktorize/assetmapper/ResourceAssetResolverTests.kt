package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import kotlin.test.Test
import kotlin.test.assertTrue

class ResourceAssetResolverTests  {

    @Test
    fun resolveBasicFile() {
        val resolver = ResourceAssetResolver("basepackage", this::class.java.classLoader)
        val asset = resolver.resolveOrNull("file1.txt")
        assertTrue { asset != null }
    }

    @Test
    fun resolveDeeperFile() {
        val resolver = ResourceAssetResolver("basepackage", this::class.java.classLoader)
        val asset = resolver.resolveOrNull("subpackage/file2.txt")
        assertTrue { asset != null }
    }

    @Test
    fun resolveAll() {
        val resolver = ResourceAssetResolver("basepackage", this::class.java.classLoader)
        val assets = resolver.resolveAll().map { it.first }
        assertTrue { assets.size == 2 }
        assertTrue { assets.contains("file1.txt") }
        assertTrue { assets.contains("subpackage/file2.txt") }
    }
}