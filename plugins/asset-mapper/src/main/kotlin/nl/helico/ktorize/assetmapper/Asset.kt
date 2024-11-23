package nl.helico.ktorize.assetmapper

import java.nio.file.Path
import kotlin.io.path.extension

sealed interface Asset {
    val path: Path
    val digest: String
    val source: ByteArray

    data class Input(
        override val path: Path,
        override val source: ByteArray,
        override val digest: String
    ) : Asset

    data class Output(
        override val source: ByteArray,
        override val digest: String,
        val input: Input,
        val dependencies: List<Output>
    ) : Asset {
        override val path: Path
            get() {
                val fileName = input.path.fileName.toString()
                val extension = input.path.extension
                val baseName = when {
                    extension.isEmpty() -> fileName
                    else -> fileName.removeSuffix(".$extension")
                }

                val parts = listOf(baseName, digest, extension).filterNot { it.isEmpty() }
                return input.path.resolveSibling(parts.joinToString("."))
            }
    }
}