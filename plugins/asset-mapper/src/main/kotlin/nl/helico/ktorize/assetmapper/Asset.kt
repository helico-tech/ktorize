package nl.helico.ktorize.assetmapper

import java.nio.file.Path

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
        override val path: Path,
        override val source: ByteArray,
        override val digest: String,
        val input: Input,
        val dependencies: List<Output>
    ) : Asset
}