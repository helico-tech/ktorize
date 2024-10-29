package nl.helico.ktorize.assetmapper

import org.junit.Test
import kotlin.test.assertEquals

class DefaultAssetMapperTests {

    val assetResolver = ResourceAssetResolver("basepackage", this::class.java.classLoader)

    @Test
    fun simpleFile() {
        val assetMapper = DefaultAssetMapper("/baseurl", assetResolver)

        val result = assetMapper.map("/baseurl/file1.txt")

        assertEquals("/baseurl/file1.2f03b03637bf162937793f756f0f1583.txt", result)
    }
}