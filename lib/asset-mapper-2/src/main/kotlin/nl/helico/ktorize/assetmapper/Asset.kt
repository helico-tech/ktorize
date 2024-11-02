package nl.helico.ktorize.assetmapper

import io.ktor.http.*
import java.nio.file.Path

sealed interface Asset {
    val path: Path
    val digest: String
    val lines: List<String>
    val contentType: ContentType

    data class Input(
        override val path: Path,
        override val lines: List<String>,
        override val digest: String
    ) : Asset {
        override val contentType: ContentType = ContentType.defaultForPath(path)
    }

    data class Output(
        override val path: Path,
        override val lines: List<String>,
        override val digest: String,
        val input: Input,
        val dependencies: List<Output>
    ) : Asset {
        override val contentType: ContentType = ContentType.defaultForPath(path)
    }
}