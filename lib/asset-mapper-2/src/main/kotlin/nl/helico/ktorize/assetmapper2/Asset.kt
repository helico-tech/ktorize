package nl.helico.ktorize.assetmapper2

import io.ktor.http.*
import java.nio.file.Path

sealed interface Asset {
    val path: Path
    val lines: List<String>
    val contentType: ContentType

    data class Input(
        override val path: Path,
        override val lines: List<String>,
    ) : Asset {
        override val contentType: ContentType = ContentType.defaultForPath(path)
    }

    data class Output(
        override val path: Path,
        override val lines: List<String>,
        val dependencies: List<Output>
    ) : Asset {
        override val contentType: ContentType = ContentType.defaultForPath(path)
    }
}