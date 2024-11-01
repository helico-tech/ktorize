package nl.helico.ktorize.assetmapper2

import java.nio.file.Path
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FileAssetWriterTests {

    @Test
    fun `writer returns writer for path`() {
        val baseDir = createTempDirectory()
        val assetWriter = FileAssetWriter(baseDir)
        val writer = assetWriter.writer(Path.of("test.txt"))
        assertNotNull(writer)

        writer.use {
            it.write("data")
        }

        val result = baseDir.resolve("test.txt").toFile().readText()
        assertNotNull(result)
        assertEquals("data", result)
    }
}